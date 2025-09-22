package org.example.expert.domain.log.service;

import lombok.RequiredArgsConstructor;
import org.example.expert.domain.log.entity.Log;
import org.example.expert.domain.log.enums.LogAction;
import org.example.expert.domain.log.enums.LogStatus;
import org.example.expert.domain.log.repository.LogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class LogService{
    private final LogRepository logRepository;

    /**
     * 비즈니스 트랜잭션과 독립적으로 커밋되어야 하므로 REQUIRES_NEW 적용
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void write(
            LogAction action,
            LogStatus status,
            Long requesterId,
            Long targetId,
            String message,
            String payload
    ){
        Log log = Log.of(action, status, requesterId, targetId, message, payload);
        logRepository.save(log);
    }
}
