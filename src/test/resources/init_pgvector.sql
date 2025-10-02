CREATE EXTENSION IF NOT EXISTS vector;

-- 키워드 임베딩을 위한 벡터 유사도 검색 인덱스 (옵션)
-- COSINE 유사도 검색을 위한 ivfflat 인덱스
-- 주의: 대량의 데이터가 있을 때만 인덱스 생성 권장 (성능상 이유)
-- CREATE INDEX IF NOT EXISTS keyword_embedding_cosine_idx 
-- ON keyword USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);

-- L2 거리 검색을 위한 인덱스 (옵션)
-- CREATE INDEX IF NOT EXISTS keyword_embedding_l2_idx 
-- ON keyword USING ivfflat (embedding vector_l2_ops) WITH (lists = 100);