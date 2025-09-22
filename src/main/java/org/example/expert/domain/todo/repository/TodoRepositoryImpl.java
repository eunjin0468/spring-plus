package org.example.expert.domain.todo.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.comment.entity.QComment;
import org.example.expert.domain.manager.entity.QManager;
import org.example.expert.domain.todo.dto.request.TodoSearchRequest;
import org.example.expert.domain.todo.dto.response.TodoSearchItem;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepositoryCustom;
import org.example.expert.domain.user.entity.QUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private static final QTodo TODO = QTodo.todo;
    private static final QManager MANAGER = QManager.manager;
    private static final QUser USER = QUser.user;
    private static final QComment COMMENT = QComment.comment;

    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        Todo result = queryFactory
                .selectFrom(TODO)
                .join(TODO.user, USER).fetchJoin()
                .where(TODO.id.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public Page<TodoSearchItem> searchPage(TodoSearchRequest req, Pageable pageable) {
        BooleanBuilder where = buildCommonPredicate(req);

        // 1) 콘텐츠 쿼리 (그룹바이 페이징 + 필요한 필드만)
        JPAQuery<TodoSearchItem> contentQuery = queryFactory
                .select(Projections.constructor(
                        TodoSearchItem.class,
                        TODO.id,
                        TODO.title,
                        MANAGER.id.countDistinct(),   // 담당자 수
                        COMMENT.id.countDistinct(),   // 댓글 수
                        TODO.createdAt
                ))
                .from(TODO)
                .leftJoin(TODO.managers, MANAGER)
                .leftJoin(TODO.comments, COMMENT);

        if (req.hasNickname()) {
            contentQuery.leftJoin(MANAGER.user, USER);
            where.and(USER.nickname.containsIgnoreCase(req.getNickname()));
        }

        List<TodoSearchItem> content = contentQuery
                .where(where)
                .groupBy(TODO.id)
                .orderBy(TODO.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 2) 카운트 쿼리 (가볍게: 댓글 조인 불필요)
        JPAQuery<Long> countQuery = queryFactory
                .select(TODO.id.countDistinct())
                .from(TODO)
                .leftJoin(TODO.managers, MANAGER);

        if (req.hasNickname()) {
            countQuery.leftJoin(MANAGER.user, USER);
        }

        Page<TodoSearchItem> page = PageableExecutionUtils.getPage(
                content,
                pageable,
                () -> {
                    Long cnt = countQuery.where(where).fetchOne();
                    return cnt != null ? cnt : 0L;
                }
        );

        // 필요 시 PageImpl로 감싸고 싶다면 아래처럼:
        return new PageImpl<>(page.getContent(), pageable, page.getTotalElements());
    }

    // === 공통 where ===
    private BooleanBuilder buildCommonPredicate(TodoSearchRequest req) {
        BooleanBuilder where = new BooleanBuilder();

        if (req.hasTitle()) {
            where.and(TODO.title.containsIgnoreCase(req.getTitle()));
        }

        LocalDate start = req.getStart();
        LocalDate end   = req.getEnd();
        LocalDate[] norm = normalizeDates(start, end);
        if (norm[0] != null) where.and(TODO.createdAt.goe(norm[0].atStartOfDay()));
        if (norm[1] != null) where.and(TODO.createdAt.lt(norm[1].plusDays(1).atStartOfDay()));

        return where;
    }

    private LocalDate[] normalizeDates(LocalDate start, LocalDate end) {
        if (start != null && end != null && start.isAfter(end)) {
            return new LocalDate[]{end, start};
        }
        return new LocalDate[]{start, end};
    }
}
