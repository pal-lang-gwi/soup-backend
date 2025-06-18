import os
import re
import requests
import logging
from typing import List, Tuple, Union, Any, Dict
from openai import OpenAI
import json

# Configure logging
logging.basicConfig(level=logging.INFO, format="%(asctime)s %(levelname)s %(message)s")
logger = logging.getLogger(__name__)

GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
REPO = os.getenv("GITHUB_REPOSITORY")

# Extract PR number from GITHUB_REF
ref = os.getenv("GITHUB_REF", "")
match = re.search(r'refs/pull/(\d+)/', ref)
if match:
    PR_NUMBER = match.group(1)
else:
    raise RuntimeError(f"PR 번호를 GITHUB_REF에서 추출할 수 없습니다: {ref}")


def get_pr_files(repo: str, pr_number: str, github_token: str) -> List[Dict[str, Any]]:
    """Fetch list of changed files for a PR from GitHub API."""
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/files"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()


def get_pr_commit_sha(repo: str, pr_number: str, github_token: str) -> str:
    """Get the head commit SHA for the PR."""
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}"
    headers = {"Authorization": f"token {github_token}"}
    response = requests.get(url, headers=headers)
    response.raise_for_status()
    return response.json()["head"]["sha"]


def extract_added_lines_with_position(patch: str) -> List[Tuple[int, str, int]]:
    """Extract added lines with file line number and diff position."""
    added: List[Tuple[int, str, int]] = []
    if not patch:
        return added

    lines = patch.split("\n")
    line_number = None
    position = 0
    for line in lines:
        position += 1
        if line.startswith('@@'):
            try:
                new_file_info = line.split(' ')[2]  # e.g. +12,7
                new_start = int(new_file_info.split(',')[0][1:])
                line_number = new_start - 1
            except Exception:
                continue
        elif line.startswith('+') and not line.startswith('+++'):
            line_number += 1
            added.append((line_number, line[1:], position))
        elif not line.startswith('-') and line_number is not None:
            line_number += 1
    return added


def normalize_reviews(response_obj: Union[Dict[str, Any], List[Any]]) -> List[Dict[str, Any]]:
    """Normalize GPT response into a list of review dicts."""
    if isinstance(response_obj, dict):
        for key in ('reviews', 'review', 'issues', 'changes'):
            if key in response_obj and isinstance(response_obj[key], list):
                return response_obj[key]
        # Single-item dict with line & issue
        if 'line' in response_obj and 'issue' in response_obj:
            return [response_obj]
        return []
    if isinstance(response_obj, list):
        return response_obj
    return []


def generate_gpt_comment_linewise(
    code_lines: List[Tuple[int, str]], pr_title: str, filename: str
) -> List[Dict[str, Any]]:
    """Call OpenAI to get line-wise review issues in JSON."""
    code_block = "\n".join(f"{ln}: {code}" for ln, code in code_lines)
    prompt = f"""
아래는 PR의 변경 코드입니다. 반드시 특정 JSON 포맷({{'reviews': [...]}})만 응답하세요.

PR 제목: {pr_title}
파일명: {filename}

변경 코드:
{code_block}
"""
    client = OpenAI(api_key=OPENAI_API_KEY)
    try:
        resp = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[{"role": "user", "content": prompt}],
            response_format={"type": "json_object"}
        )
        content = resp.choices[0].message.content
        raw = json.loads(content)
        reviews = normalize_reviews(raw)
        return reviews
    except Exception as e:
        logger.error("GPT linewise parsing error: %s, content=%s", e, resp.choices[0].message.content if 'resp' in locals() else None)
        return []


def post_inline_comment(
    repo: str, pr_number: str, commit_id: str,
    path: str, body: str, position: int, github_token: str
) -> None:
    """Post an inline comment on the PR via GitHub API."""
    url = f"https://api.github.com/repos/{repo}/pulls/{pr_number}/comments"
    headers = {
        "Authorization": f"token {github_token}",
        "Accept": "application/vnd.github+json"
    }
    payload = {"body": body, "commit_id": commit_id, "path": path, "position": position, "side": "RIGHT"}
    response = requests.post(url, headers=headers, json=payload)
    response.raise_for_status()
    logger.info("Inline comment posted at %s:%s", path, position)


def main() -> None:
    """Main workflow: fetch PR, extract diffs, call GPT, and post comments."""
    logger.info("Fetching PR files and commit SHA...")
    pr_files = get_pr_files(REPO, PR_NUMBER, GITHUB_TOKEN)
    commit_id = get_pr_commit_sha(REPO, PR_NUMBER, GITHUB_TOKEN)

    logger.info(f"Processing {len(pr_files)} changed files in PR #{PR_NUMBER}")
    for file in pr_files:
        filename = file.get('filename', '')
        patch = file.get('patch') or ''
        if not patch:
            logger.info(f"Skipping {filename}: no patch found.")
            continue
        added = extract_added_lines_with_position(patch)
        if not added:
            logger.info(f"Skipping {filename}: no added lines found.")
            continue

        code_lines = [(ln, code) for ln, code, _ in added]
        logger.info(f"Requesting GPT review for {filename} ({len(code_lines)} added lines)...")
        reviews = generate_gpt_comment_linewise(code_lines, os.getenv("PR_TITLE", ""), filename)
        logger.info(f"Received {len(reviews)} review items for {filename}")
        for idx, item in enumerate(reviews):
            issue = item.get('issue')
            if issue:
                suggestion = item.get('suggestion')
                refactor = item.get('refactor')
                body = f"⚠️ {issue}\n"
                if suggestion:
                    body += f"💡 {suggestion}\n"
                if refactor:
                    body += f"```java\n{refactor}\n```"
                position = added[idx][2]
                logger.info(f"Posting inline comment for {filename} at diff position {position} (line {added[idx][0]})...")
                try:
                    post_inline_comment(REPO, PR_NUMBER, commit_id, filename, body, position, GITHUB_TOKEN)
                except Exception as e:
                    logger.error(f"Failed to post inline comment for {filename} at position {position}: {e}")
    logger.info("Auto review script completed.")

if __name__ == "__main__":
    main()
