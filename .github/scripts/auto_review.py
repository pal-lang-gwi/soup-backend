import os
import re
import requests
from openai import OpenAI
import json

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
REPO = os.getenv("GITHUB_REPOSITORY")

ref = os.getenv("GITHUB_REF", "")
match = re.search(r'refs/pull/(\d+)/', ref)
if match:
    PR_NUMBER = match.group(1)
else:
    raise Exception(f"PR 번호를 GITHUB_REF에서 추출할 수 없습니다: {ref}")

def get_pr_files(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def get_pr_commit_sha(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()['head']['sha']

def extract_added_lines(patch):
    added_lines = []
    if not patch:
        return added_lines

    lines = patch.split('\n')
    line_number = None
    for line in lines:
        if line.startswith('@@'):
            try:
                parts = line.split(' ')
                new_file_info = parts[2]  # 예: +12,7
                new_start_line = int(new_file_info.split(',')[0][1:])
                line_number = new_start_line - 1
            except:
                continue
        elif line.startswith('+') and not line.startswith('+++'):
            line_number += 1
            added_lines.append((line_number, line[1:]))
        elif not line.startswith('-'):
            if line_number is not None:
                line_number += 1
    return added_lines

def generate_gpt_comment_linewise(code_lines, pr_title, filename):
    # code_lines: [(line_num, code), ...]
    code_block = "\n".join([f"{line_num}: {code}" for line_num, code in code_lines])
    prompt = f"""아래는 PR의 변경 코드입니다. 각 라인별로 실제로 리뷰가 필요한 이슈가 있는지 판단해서, 아래 JSON 배열 형식으로 답변하세요.

PR 제목: {pr_title}
파일명: {filename}

변경 코드:
{code_block}

응답 예시:
[
  {{ "line": 24, "issue": "userInfo.getRole()이 null일 수 있음", "suggestion": "Role.GUEST로 대체" }},
  {{ "line": 30, "issue": null }}
]

- 이슈가 없는 라인은 반드시 issue: null로 명시
- 사소한 변경, 의미 없는 수정, 문제 없는 라인은 반드시 issue: null로 명시
- suggestion은 있을 때만 작성
"""
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}],
        response_format={ "type": "json_object" }
    )
    content = response.choices[0].message.content
    try:
        result = json.loads(content)
        return result
    except Exception as e:
        print(f"[ERROR] Failed to parse GPT response as JSON: {e}")
        return []

def generate_gpt_comment(code_snippet, pr_title, changed_files):
    prompt = f"""다음 PR의 코드 변경사항을 분석해주세요:

PR 제목: {pr_title}
변경된 파일들: {changed_files}

변경된 코드:
{code_snippet}

각 카테고리별로 문제점이 있다면 해당 내용을 작성하고, 없다면 "해당 사항 없음"이라고 작성해주세요.
특히 작은 변경사항이나 단순한 수정의 경우, 불필요한 리뷰를 생성하지 말고 "해당 사항 없음"으로 처리해주세요."""
    
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}],
        response_format={ "type": "json_object" }
    )
    content = response.choices[0].message.content
    
    try:
        result = json.loads(content)
        
        # PR Description 생성
        description = f"""## 변경 사항 요약
{result['description']['summary']}

## 주요 변경 내용
{result['description']['details']}"""

        # 시퀀스 다이어그램이 있는 경우 추가
        if result['description'].get('sequence_diagram'):
            description += f"""

## 코드 흐름도
```mermaid
{result['description']['sequence_diagram']}
```"""
        
        # 코드 리뷰 생성
        review = "## 코드 리뷰\n\n"
        
        # 각 카테고리별 리뷰 생성
        categories = {
            "functionality": "기능성",
            "security": "보안",
            "performance": "성능",
            "testing": "테스트",
            "documentation": "문서화"
        }
        
        has_any_issues = False
        for category, korean_name in categories.items():
            category_review = result['review'].get(category, {})
            if category_review.get('issues'):
                has_any_issues = True
                review += f"### {korean_name}\n\n"
                for issue in category_review['issues']:
                    review += f"#### {issue['type'].upper()}\n"
                    review += f"- 심각도: {issue['severity']}\n"
                    review += f"- 문제: {issue['message']}\n"
                    if issue.get('line'):
                        review += f"- 위치: {issue['line']}번 라인\n"
                    if issue.get('suggestion'):
                        review += f"- 제안: {issue['suggestion']}\n"
                    review += "\n"
            else:
                review += f"### {korean_name}\n해당 사항 없음\n\n"
        
        if not has_any_issues:
            review = "## 코드 리뷰\n\n모든 카테고리에 대해 특별한 문제점이 발견되지 않았습니다."
        
        return description, review
    except json.JSONDecodeError as e:
        print(f"[ERROR] Failed to parse GPT response as JSON: {e}")
        return content, "리뷰 생성 중 오류가 발생했습니다."

def post_pr_comment(repo, pr_number, body, github_token):
    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {
        "Authorization": f"token {github_token}",
        "Accept": "application/vnd.github+json"
    }
    payload = {
        "body": body
    }
    response = requests.post(url, headers=headers, json=payload)
    response.raise_for_status()

def generate_pr_description(pr_title, changed_files, code_summary):
    prompt = f"""PR 제목: {pr_title}
변경된 파일들: {changed_files}
변경된 코드 요약:
{code_summary}

위 내용을 참고하여 다음 PR에 대한 설명을 작성해주세요:

다음 형식으로 작성해주세요:
1. 변경 사항 요약
2. 주요 변경 내용"""
    
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content

def update_pr_description(repo, pr_number, description, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"
    headers = {
        "Authorization": f"token {github_token}",
        "Accept": "application/vnd.github+json"
    }
    payload = {
        "body": description
    }
    response = requests.patch(url, headers=headers, json=payload)
    response.raise_for_status()

def post_inline_comment(repo, pr_number, commit_id, path, body, line, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {
        "Authorization": f"token {github_token}",
        "Accept": "application/vnd.github+json"
    }
    payload = {
        "body": body,
        "commit_id": commit_id,
        "path": path,
        "side": "RIGHT",
        "line": line
    }
    response = requests.post(url, headers=headers, json=payload)
    response.raise_for_status()

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    changed_filenames = []
    code_summaries = []

    # 리뷰가 필요한 파일 확장자
    REVIEWABLE_EXTENSIONS = {
        '.java', '.kt', '.py', '.js', '.ts', '.jsx', '.tsx',  # 소스 코드
        '.xml', '.yml', '.yaml', '.properties',  # 설정 파일
        '.sql'  # 데이터베이스
    }

    # 무시할 파일/디렉토리 패턴
    IGNORE_PATTERNS = {
        'test/', 'tests/', '__tests__/',  # 테스트 파일
        'node_modules/', 'target/', 'build/',  # 빌드 결과물
        '.git/', '.github/',  # Git 관련
        '*.md', '*.txt', '*.log', '.gitignore'  # 문서 파일
    }

    pr_title = os.getenv("PR_TITLE", "")
    commit_id = get_pr_commit_sha(REPO, PR_NUMBER, GITHUB_TOKEN)

    for file in pr_files:
        filename = file["filename"]
        # 무시할 파일인지 확인
        if any(filename.endswith(pattern) for pattern in IGNORE_PATTERNS):
            continue
        # 리뷰가 필요한 파일인지 확인
        if not any(filename.endswith(ext) for ext in REVIEWABLE_EXTENSIONS):
            continue
        changed_filenames.append(filename)
        patch = file.get("patch")
        if not patch:
            continue
        added_lines = extract_added_lines(patch)
        if not added_lines:
            continue
        # 주요 변경 코드 요약용
        code_summary = f"### {filename}\n" + "\n".join([f"Line {line_num}: {code}" for line_num, code in added_lines])
        code_summaries.append(code_summary)
        # 라인별 GPT 리뷰 요청 및 인라인 코멘트
        linewise_issues = generate_gpt_comment_linewise(added_lines, pr_title, filename)
        for item in linewise_issues:
            if item.get("issue"):
                body = f"⚠️ {item['issue']}\n"
                if item.get("suggestion"):
                    body += f"💡 {item['suggestion']}"
                post_inline_comment(REPO, PR_NUMBER, commit_id, filename, body, item["line"], GITHUB_TOKEN)
                print(f"[SUCCESS] Inline comment for {filename} line {item['line']}")

    # PR 본문 자동 요약/설명 생성 및 업데이트
    if changed_filenames and code_summaries:
        try:
            code_summary_text = "\n\n".join(code_summaries)
            pr_description = generate_pr_description(pr_title, changed_filenames, code_summary_text)
            update_pr_description(REPO, PR_NUMBER, pr_description, GITHUB_TOKEN)
            print(f"[SUCCESS] PR 본문이 성공적으로 업데이트되었습니다.")
        except Exception as e:
            print(f"[ERROR] Failed to update PR description: {e}")
    else:
        print("[INFO] PR 본문에 반영할 변경사항이 없습니다.")

if __name__ == "__main__":
    main()