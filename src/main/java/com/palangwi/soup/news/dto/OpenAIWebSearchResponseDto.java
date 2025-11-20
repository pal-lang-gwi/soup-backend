package com.palangwi.soup.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenAIWebSearchResponseDto(
        List<Output> output,
        Usage usage
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Output(
            String type,
            List<Content> content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Content(
            String type,
            String text
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Usage(
            int total_tokens
    ) {}
}
