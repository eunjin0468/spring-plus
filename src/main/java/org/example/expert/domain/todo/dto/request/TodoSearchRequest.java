package org.example.expert.domain.todo.dto.request;

import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class TodoSearchRequest {
    private String weather;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDate start;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE_TIME)
    private LocalDate end;

    public TodoSearchRequest(String weather, LocalDate start, LocalDate end) {
        this.weather = weather;
        this.start = start;
        this.end = end;
    }
}
