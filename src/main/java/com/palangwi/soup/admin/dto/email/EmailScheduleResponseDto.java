package com.palangwi.soup.admin.dto.email;

public record EmailScheduleResponseDto(String lastStatus, String lastExecutionTime, String nextExecutionTime, int activeTasks) {
    public static EmailScheduleResponseDto of(String lastStatus, String lastExecutionTime, String nextExecutionTime, int activeTasks) {
        return new EmailScheduleResponseDto(lastStatus, lastExecutionTime, nextExecutionTime, activeTasks);
    }
}
