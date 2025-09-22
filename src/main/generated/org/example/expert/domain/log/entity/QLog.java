package org.example.expert.domain.log.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QLog is a Querydsl query type for Log
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLog extends EntityPathBase<Log> {

    private static final long serialVersionUID = 2144785399L;

    public static final QLog log = new QLog("log");

    public final EnumPath<org.example.expert.domain.log.enums.LogAction> action = createEnum("action", org.example.expert.domain.log.enums.LogAction.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath message = createString("message");

    public final StringPath payload = createString("payload");

    public final NumberPath<Long> requesterId = createNumber("requesterId", Long.class);

    public final EnumPath<org.example.expert.domain.log.enums.LogStatus> status = createEnum("status", org.example.expert.domain.log.enums.LogStatus.class);

    public final NumberPath<Long> targetId = createNumber("targetId", Long.class);

    public QLog(String variable) {
        super(Log.class, forVariable(variable));
    }

    public QLog(Path<? extends Log> path) {
        super(path.getType(), path.getMetadata());
    }

    public QLog(PathMetadata metadata) {
        super(Log.class, metadata);
    }

}

