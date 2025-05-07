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
    prompt = f"다음 코드에 대해 리뷰를 작성해주세요 (400자 이내):\n\n{code_snippet}\n\n포맷:\n## 이슈\n- 문제 설명\n## 제안\n- 개선 방법"
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

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    commit_sha = get_pr_commit_sha(REPO, PR_NUMBER, GITHUB_TOKEN)

    for file in pr_files:
        filename = file["filename"]
        patch = file.get("patch")
        if not patch or filename.endswith(('.md', '.txt', '.log', '.gitignore')):
            continue

        added_lines = extract_added_lines(patch)
        for line_num, code_line in added_lines:
            try:
                comment = generate_gpt_comment(code_line)
                post_inline_review_comment(REPO, PR_NUMBER, commit_sha, filename, line_num, comment, GITHUB_TOKEN)
            except Exception as e:
                print(f"[ERROR] Failed to comment on {filename} line {line_num}: {e}")

if __name__ == "__main__":
    main()