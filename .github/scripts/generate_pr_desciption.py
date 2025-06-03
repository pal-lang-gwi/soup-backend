import os
import requests
import json

OPENAI_API_KEY = os.environ["OPENAI_API_KEY"]
PR_TITLE = os.environ["PR_TITLE"]
CHANGED_FILES = os.environ["CHANGED_FILES"]

prompt = f"""다음 PR에 대한 설명을 작성해주세요:
PR 제목: {PR_TITLE}
변경된 파일들: {CHANGED_FILES}

다음 형식으로 작성해주세요:
1. 변경 사항 요약
2. 주요 변경 내용
3. 테스트 방법
4. 관련 이슈
"""

headers = {
    "Authorization": f"Bearer {OPENAI_API_KEY}",
    "Content-Type": "application/json"
}

data = {
    "model": "gpt-4o-mini",
    "messages": [{"role": "user", "content": prompt}]
}

response = requests.post(
    "https://api.openai.com/v1/chat/completions",
    headers=headers,
    data=json.dumps(data)
)

print(response.text)  # 전체 응답 출력(디버깅용)
result = response.json()
print(result.get("choices", [{}])[0].get("message", {}).get("content"))