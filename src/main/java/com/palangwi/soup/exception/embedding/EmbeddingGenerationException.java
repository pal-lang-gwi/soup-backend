package com.palangwi.soup.exception.embedding;

/**
 * 임베딩 생성 실패 시 발생하는 예외
 */
public class EmbeddingGenerationException extends RuntimeException {

    public EmbeddingGenerationException(String message) {
        super(message);
    }

    public EmbeddingGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
