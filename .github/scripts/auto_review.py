import os
import re
import requests
import openai

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
REPO = os.getenv("GITHUB_REPOSITORY")

# PR 번호 추출 (이렇게 수정!)
ref = os.getenv("GITHUB_REF", "")
match = re.search(r'refs/pull/(\d+)/', ref)
if match:
    PR_NUMBER = match.group(1)
else:
    raise Exception(f"PR 번호를 GITHUB_REF에서 추출할 수 없습니다: {ref}")

def get_pr_files(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {"Authorization": f"token {github_token}"}
    print(f"[INFO] PR 파일 목록 조회: {url}")
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
            added_lines.append((line_number, line[1:]))
        elif not line.startswith('-'):
            if line_number is not None:
                line_number += 1
    print(f"[INFO] 추가된 라인 추출 완료: {len(added_lines)}개")
    return added_lines

def generate_comment_by_gpt(code_line):
    prompt = f"다음 코드 한 줄에 대해 리뷰어처럼 개선할 점이나 칭찬할 점을 짧게 작성해줘.\n\n코드:\n{code_line}"
    print(f"[INFO] GPT에 코드 리뷰 요청: {code_line[:30]}...")
    try:
        client = openai.OpenAI(api_key=OPENAI_API_KEY)
        response = client.chat.completions.create(
            model="gpt-4",
            messages=[{"role": "user", "content": prompt}]
        )
        return response.choices[0].message.content
    except Exception as e:
        print(f"[ERROR] GPT 호출 실패: {e}")
        return "GPT 호출 실패: " + str(e)

def post_inline_comment(repo, pr_number, body, path, line, github_token):
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    payload = {
        "body": body,
        "path": path,
        "side": "RIGHT",
        "line": line
    }
    print(f"[INFO] PR 인라인 코멘트 등록: {path}:{line}")
    try:
        response = requests.post(url, headers=headers, json=payload)
        response.raise_for_status()
    except Exception as e:
        print(f"[ERROR] 코멘트 등록 실패: {e}")

def main():
    try:
        pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    except Exception as e:
        print(f"[ERROR] PR 파일 목록 조회 실패: {e}")
        return

    for file in pr_files:
        filename = file.get("filename")
        patch = file.get("patch")
        print(f"[INFO] 파일 처리 시작: {filename}")

        added_lines = extract_added_lines(patch)

        for line_number, code in added_lines:
            comment = generate_comment_by_gpt(code)
            post_inline_comment(REPO, PR_NUMBER, comment, filename, line_number, GITHUB_TOKEN)

if __name__ == "__main__":
    main()
