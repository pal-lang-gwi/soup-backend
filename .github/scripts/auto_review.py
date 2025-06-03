import os
import re
import requests
from openai import OpenAI

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

def generate_gpt_comment(code_snippet):
    prompt = f"""다음 PR의 코드 변경사항에서 발견되는 치명적인 문제점만 알려주세요:

{code_snippet}

다음 형식으로 작성해주세요:
## 치명적인 문제점
- 발견된 심각한 버그나 보안 취약점
- 성능에 심각한 영향을 미치는 문제
- 시스템 안정성을 해치는 문제
- 코드 가독성을 해치는 문제


(문제점이 없다면 "발견된 치명적인 문제점이 없습니다."라고만 작성해주세요)"""
    
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content

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

def generate_pr_description():
    prompt = f"""다음 PR에 대한 설명을 작성해주세요:

다음 형식으로  작성해주세요:
1. 변경 사항 요약
2. 주요 변경 내용
"""
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

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    all_changes = []
    changed_filenames = []

    for file in pr_files:
        filename = file["filename"]
        changed_filenames.append(filename)
        patch = file.get("patch")
        
        if not patch or filename.endswith(('.md', '.txt', '.log', '.gitignore')):
            continue

        added_lines = extract_added_lines(patch)
        if not added_lines:
            continue

        # 파일의 모든 변경사항을 하나의 문자열로 모음
        file_changes = f"\n### {filename}\n" + "\n".join([f"Line {line_num}: {code}" for line_num, code in added_lines])
        all_changes.append(file_changes)
    
    if all_changes:
        try:
            # PR 전체에 대한 하나의 리뷰 생성
            all_changes_text = "\n".join(all_changes)
            comment = generate_gpt_comment(all_changes_text)
            post_pr_comment(REPO, PR_NUMBER, comment, GITHUB_TOKEN)
            print(f"[SUCCESS] Added review to PR #{PR_NUMBER}")
        except Exception as e:
            print(f"[ERROR] Failed to comment on PR: {e}")

    # === PR description 자동 업데이트 ===
    try:
        pr_title = os.getenv("PR_TITLE", "")
        changed_files_str = ", ".join(changed_filenames)
        pr_description = generate_pr_description()
        update_pr_description(REPO, PR_NUMBER, pr_description, GITHUB_TOKEN)
        print(f"[SUCCESS] PR 본문이 성공적으로 업데이트되었습니다.")
    except Exception as e:
        print(f"[ERROR] PR 본문 업데이트 실패: {e}")

if __name__ == "__main__":
    main()