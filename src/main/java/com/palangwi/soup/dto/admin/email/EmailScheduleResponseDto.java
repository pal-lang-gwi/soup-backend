package com.palangwi.soup.dto.admin.email;

public record EmailScheduleResponseDto(String lastStatus, String lastExecutionTime, String nextExecutionTime, int activeTasks) {
    public static EmailScheduleResponseDto of(String lastStatus, String lastExecutionTime, String nextExecutionTime, int activeTasks) {
        return new EmailScheduleResponseDto(lastStatus, lastExecutionTime, nextExecutionTime, activeTasks);
    }
}
