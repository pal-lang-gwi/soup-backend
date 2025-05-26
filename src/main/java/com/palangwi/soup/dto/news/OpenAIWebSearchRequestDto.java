package com.palangwi.soup.dto.news;

import java.util.List;

public record OpenAIWebSearchRequestDto(
        String model,
        List<Tool> tools,
        String input,
        boolean parallel_tool_calls,
        String tool_choice
) {
    public record Tool(String type) {}
}