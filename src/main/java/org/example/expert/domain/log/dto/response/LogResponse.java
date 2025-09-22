package org.example.expert.domain.log.dto.response;

import java.time.LocalDateTime;


import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.enums.LogAction;
import org.example.expert.domain.log.enums.LogStatus;

public record LogResponse(
        Long id,
        LogAction action,
        LogStatus status,
        Long requesterId,
        Long targetId,
        String message,
        LocalDateTime createdAt
) {
    public static LogResponse from(Log log) {
        return new LogResponse(
                log.getId(),
                log.getAction(),
                log.getStatus(),
                log.getRequesterId(),
                log.getTargetId(),
                log.getMessage(),
                log.getCreatedAt()
        );
    }
}
