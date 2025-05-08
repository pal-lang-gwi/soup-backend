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
    prompt = f"""다음 코드 변경사항에 대해 리뷰를 작성해주세요 (400자 이내):

{code_snippet}

다음 형식으로 작성해주세요:
## 주요 변경사항
- 변경된 내용 요약

## 이슈
- 발견된 문제점들

## 제안
- 개선 방안"""
    
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o-mini",
        messages=[{"role": "user", "content": prompt}]
    )
    return response.choices[0].message.content

def post_inline_review_comment(repo, pr_number, commit_id, path, line, body, github_token):
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

def get_file_comments(repo, pr_number, path, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    
    # 특정 파일의 코멘트만 필터링
    file_comments = [comment for comment in response.json() if comment['path'] == path]
    return len(file_comments)

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    commit_sha = get_pr_commit_sha(REPO, PR_NUMBER, GITHUB_TOKEN)

    for file in pr_files:
        filename = file["filename"]
        patch = file.get("patch")
        
        # 이미 코멘트가 있는 파일은 스킵
        comment_count = get_file_comments(REPO, PR_NUMBER, filename, GITHUB_TOKEN)
        if comment_count > 0:
            print(f"[SKIP] {filename} already has {comment_count} comments")
            continue

        if not patch or filename.endswith(('.md', '.txt', '.log', '.gitignore')):
            continue

        added_lines = extract_added_lines(patch)
        if not added_lines:
            continue

        # 파일의 모든 변경사항을 하나의 문자열로 모음
        file_changes = "\n".join([f"Line {line_num}: {code}" for line_num, code in added_lines])
        
        try:
            # 파일 전체에 대한 하나의 리뷰 생성
            comment = generate_gpt_comment(file_changes)
            # 첫 번째 변경된 라인에 리뷰 달기
            first_line = added_lines[0][0]
            post_inline_review_comment(REPO, PR_NUMBER, commit_sha, filename, first_line, comment, GITHUB_TOKEN)
            print(f"[SUCCESS] Added review to {filename}")
        except Exception as e:
            print(f"[ERROR] Failed to comment on {filename}: {e}")

if __name__ == "__main__":
    main()