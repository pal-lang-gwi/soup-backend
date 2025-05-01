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
    print(f"[INFO] PR 파일 목록 조회: {url}")
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] API 요청 실패: {str(e)}")
        print(f"[ERROR] 상태 코드: {e.response.status_code if hasattr(e, 'response') else 'N/A'}")
        print(f"[ERROR] 응답 내용: {e.response.text if hasattr(e, 'response') else 'N/A'}")
        raise

def extract_added_lines(patch):
    added_lines = []
    if patch is None:
        print("[WARN] patch가 None입니다.")
        return added_lines

    try:
        lines = patch.split('\n')
        line_number = None
        for line in lines:
            if line.startswith('@@'):
                try:
                    parts = line.split(' ')
                    new_file_info = parts[2]  # 예시: +12,7
                    new_start_line = int(new_file_info.split(',')[0][1:])
                    line_number = new_start_line - 1
                except (IndexError, ValueError) as e:
                    print(f"[ERROR] patch 헤더 파싱 실패: {line}")
                    print(f"[ERROR] 상세 에러: {str(e)}")
                    continue
            elif line.startswith('+') and not line.startswith('+++'):
                line_number += 1
                added_lines.append((line_number, line[1:]))
            elif not line.startswith('-'):
                if line_number is not None:
                    line_number += 1
        print(f"[INFO] 추가된 라인 추출 완료: {len(added_lines)}개")
        return added_lines
    except Exception as e:
        print(f"[ERROR] patch 처리 중 오류 발생: {str(e)}")
        print(f"[ERROR] patch 내용: {patch[:200]}...")  # 처음 200자만 출력
        raise

def generate_comment_by_gpt(code_changes, filename, total_changes_count):
    prompt = f"""다음 PR의 '{filename}' 파일 변경사항({total_changes_count}줄)을 검토해주세요.

변경사항:
{code_changes}

다음 우선순위로 400자 이내의 간단한 리뷰를 작성해주세요:

1. 치명적인 버그나 보안 이슈 (있는 경우 최우선 언급)
2. 성능 문제 (비효율적인 로직, 메모리 누수 등)
3. 에러 처리 미흡
4. 재사용성/유지보수성 문제

리뷰 형식:
## 주요 이슈
[발견된 치명적/중요 문제만 기술]

## 개선방안
[구체적인 수정 방법 1-2개만 제시]"""

    print(f"[INFO] GPT에 코드 리뷰 요청")
    try:
        if not OPENAI_API_KEY:
            raise ValueError("OPENAI_API_KEY가 설정되지 않았습니다.")
        
        client = OpenAI(api_key=OPENAI_API_KEY)
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "user", "content": prompt}]
        )
        return response.choices[0].message.content
    except Exception as e:
        print(f"[ERROR] GPT 호출 실패: {str(e)}")
        print(f"[ERROR] API 키 설정 여부: {bool(OPENAI_API_KEY)}")
        print(f"[ERROR] 요청 프롬프트: {prompt}")
        return f"GPT 호출 실패: {str(e)}"

def should_review_file(filename, changes):
    # 리뷰 제외할 파일들
    if any(filename.endswith(ext) for ext in ['.md', '.txt', '.log', '.gitignore']):
        return False
    return True

def get_review_strategy(changes_count):
    if changes_count > 500:
        return "주요 로직 변경과 아키텍처 영향도만 검토"
    elif changes_count > 200:
        return "중요 함수 수준의 변경사항 검토"
    else:
        return "모든 변경사항 상세 검토"

def get_previous_reviews(repo, pr_number, github_token):
    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    try:
        response = requests.get(url, headers=headers)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        print(f"[ERROR] 이전 리뷰 조회 실패: {str(e)}")
        return []

def avoid_duplicate_comments(new_comment, previous_reviews):
    # 간단한 중복 체크 (유사도 검사는 생략)
    for review in previous_reviews:
        if review['body'] == new_comment:
            return True
    return False

def post_inline_comment(repo, pr_number, body, path, line, github_token):
    url = f"https://api.github.com/repos/{repo}/issues/{pr_number}/comments"
    headers = {"Authorization": f"token {github_token}"}
    payload = {
        "body": body
    }
    print(f"[INFO] PR 코멘트 등록 시도")
    try:
        response = requests.post(url, headers=headers, json=payload)
        response.raise_for_status()
        print(f"[INFO] 코멘트 등록 성공")
    except requests.exceptions.RequestException as e:
        print(f"[ERROR] 코멘트 등록 실패: {str(e)}")
        print(f"[ERROR] 상태 코드: {e.response.status_code if hasattr(e, 'response') else 'N/A'}")
        print(f"[ERROR] 응답 내용: {e.response.text if hasattr(e, 'response') else 'N/A'}")
        print(f"[ERROR] 요청 URL: {url}")
        print(f"[ERROR] 요청 본문: {payload}")
        raise

def main():
    print("[INFO] 프로그램 시작")
    print(f"[INFO] 환경변수 설정 상태:")
    print(f"  - GITHUB_TOKEN: {'설정됨' if GITHUB_TOKEN else '미설정'}")
    print(f"  - OPENAI_API_KEY: {'설정됨' if OPENAI_API_KEY else '미설정'}")
    print(f"  - REPO: {REPO}")
    print(f"  - PR_NUMBER: {PR_NUMBER}")

    try:
        pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
        previous_reviews = get_previous_reviews(REPO, PR_NUMBER, GITHUB_TOKEN)
    except Exception as e:
        print(f"[ERROR] 초기 데이터 조회 실패: {str(e)}")
        return

    for file in pr_files:
        filename = file.get("filename")
        patch = file.get("patch")
        changes_count = len(patch.split('\n')) if patch else 0

        if not should_review_file(filename, changes_count):
            print(f"[INFO] 리뷰 제외 파일: {filename}")
            continue

        print(f"[INFO] 파일 처리 시작: {filename} (변경: {changes_count}줄)")
        
        try:
            added_lines = extract_added_lines(patch)
            if not added_lines:
                continue

            code_changes = "\n".join([f"Line {line_number}: {code}" for line_number, code in added_lines])
            review_strategy = get_review_strategy(changes_count)
            
            comment = generate_comment_by_gpt(code_changes, filename, changes_count)
            
            if comment.startswith("GPT 호출 실패"):
                continue

            if not avoid_duplicate_comments(comment, previous_reviews):
                full_comment = f"## 파일: {filename}\n\n{comment}"
                post_inline_comment(REPO, PR_NUMBER, full_comment, filename, 0, GITHUB_TOKEN)
            else:
                print(f"[INFO] 중복 리뷰 제외: {filename}")

        except Exception as e:
            print(f"[ERROR] {filename} 처리 실패: {str(e)}")
            continue

def test():
    print("[INFO] 테스트 함수 시작")
    api_key = os.getenv("OPENAI_API_KEY")
    print(f"[DEBUG] API 키 존재 여부: {'있음' if api_key else '없음'}")
    
    try:
        if not api_key:
            raise ValueError("OPENAI_API_KEY가 설정되지 않았습니다.")
            
        client = OpenAI(api_key=api_key)
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "user", "content": "print('Hello, World!')"}]
        )
        print(f"[INFO] GPT 응답: {response.choices[0].message.content}")
    except Exception as e:
        error_msg = f"GPT 호출 실패: {str(e)}"
        print(f"[ERROR] {error_msg}")
        print(f"[ERROR] 상세 에러 정보: {type(e).__name__}")
        return error_msg

if __name__ == "__main__":
    print("[INFO] 프로그램 실행 시작")
    main()
    # test()