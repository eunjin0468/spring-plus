package org.example.expert.domain.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoSearchRequest {
    private String title;
    private String nickname;
    private String weather;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate start;

    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate end;

    public boolean hasTitle(){
        return title !=null && !title.isBlank();
    }

    public boolean hasNickname(){
        return nickname!=null && !nickname.isBlank();
    }
}
