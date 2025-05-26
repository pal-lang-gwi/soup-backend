package com.palangwi.soup.dto.news;

import java.util.List;

public record OpenAIWebSearchResponseDto(
        List<Output> output
) {
    public record Output(
            String id,
            String type,
            String status
    ) {}

    public record MessageOutput(
            String id,
            String type,
            String status,
            String role,
            List<Content> content
    ) {}

    public record Content(
            String type,
            String text,
            List<Annotation> annotations
    ) {}

    public record Annotation(
            String type,
            int start_index,
            int end_index,
            String url,
            String title
    ) {}
}
