import os
import requests
import openai

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
REPO = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER = os.getenv("GITHUB_REF").split('/')[-1]

def get_pr_files(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def extract_added_lines(patch):
    added_lines = []
    if patch is None:
        return added_lines

    lines = patch.split('\n')
    line_number = None
    for line in lines:
        if line.startswith('@@'):
            parts = line.split(' ')
            new_file_info = parts[2]  # 예시: +12,7
            new_start_line = int(new_file_info.split(',')[0][1:])
            line_number = new_start_line - 1
        elif line.startswith('+') and not line.startswith('+++'):
            line_number += 1
            added_liimport os
import requests
import openai

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
REPO = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER = os.getenv("GITHUB_REF").split('/')[-1]

def get_pr_files(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()

def generate_comment_by_gpt(patch, filename):
    openai.api_key = OPENAI_API_KEY
    prompt = f"""
파일 이름: {filename}

다음은 PR에서 수정된 전체 코드 변경사항입니다.
이 파일 변경사항에 대해 종합적인 코드 리뷰를 작성해줘.
좋은 점, 개선할 점을 모두 포함하고 구체적으로 작성해줘.

변경된 코드:
{patch}
"""
    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}]
    )
    return response['choices'][0]['message']['content']

def post_inline_comment(repo, pr_number, body, path, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    payload = {
        "body": body,
        "path": path,
        "side": "RIGHT",
        "line": 1  # 파일의 첫 줄에 코멘트를 답니다
    }
    response = requests.post(url, headers=headers, json=payload)
    response.raise_for_status()

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)

    for file in pr_files:
        filename = file.get("filename")
        patch = file.get("patch")

        if patch:  # 변경사항이 있는 파일만
            comment = generate_comment_by_gpt(patch, filename)
            post_inline_comment(REPO, PR_NUMBER, comment, filename, GITHUB_TOKEN)

if __name__ == "__main__":
    main()
nes.append((line_number, line[1:]))
        elif not line.startswith('-'):
            line_number += 1
    return added_lines

def generate_comment_by_gpt(code_line):
    openai.api_key = OPENAI_API_KEY
    prompt = f"다음 코드 한 줄에 대해 리뷰어처럼 개선할 점이나 칭찬할 점을 짧게 작성해줘.\n\n코드:\n{code_line}"
    response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[{"role": "user", "content": prompt}]
    )
    return response['choices'][0]['message']['content']

def post_inline_comment(repo, pr_number, body, path, line, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    payload = {
        "body": body,
        "path": path,
        "side": "RIGHT",
        "line": line
    }
    response = requests.post(url, headers=headers, json=payload)
    response.raise_for_status()

def main():
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)

    for file in pr_files:
        filename = file.get("filename")
        patch = file.get("patch")

        added_lines = extract_added_lines(patch)

        for line_number, code in added_lines:
            comment = generate_comment_by_gpt(code)
            post_inline_comment(REPO, PR_NUMBER, comment, filename, line_number, GITHUB_TOKEN)

if __name__ == "__main__":
    main()
