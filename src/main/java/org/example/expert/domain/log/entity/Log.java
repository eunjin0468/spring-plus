package org.example.expert.domain.log.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.example.expert.domain.log.enums.LogAction;
import org.example.expert.domain.log.enums.LogStatus;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(
        name = "logs",
        indexes = {
                @Index(name = "ix_log_created_at", columnList = "created_at"),
                @Index(name = "ix_log_action",     columnList = "action"),
                @Index(name = "ix_log_status",     columnList = "status")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Log {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 작업에 대한 로그인지
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private LogAction action;

    //성공/실패
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LogStatus status;

    // 누가 요청했는지(선택)
    @Column(name = "requester_id")
    private Long requesterId;

    // 대상 리소스 id
    @Column(name = "target_id")
    private Long targetId;

    // 요약/오류 메시지(선택)
    @Column(length = 500)
    private String message;

    // 상세 페이로드(요청/응답/컨텍스트)
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private String payload;

    //생성 시각 – NOT NULL 보장
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private Log(LogAction action, LogStatus status, Long requesterId, Long targetId, String message, String payload) {
        this.action = action;
        this.status = status;
        this.requesterId = requesterId;
        this.targetId = targetId;
        this.message = message;
        this.payload = payload;
    }

    public static Log of(LogAction action, LogStatus status,
                         Long requesterId, Long targetId,
                         String message, String payload) {
        return new Log(action, status, requesterId, targetId, message, payload);
    }
}
