# JPA 심화 · 테스트 코드 · 성능 최적화

## 주요 기능
* **할 일(Todo)**: 생성/조회/검색(QueryDSL·Projections·페이징)
* **댓글(Comment)**: Fetch Join·EntityGraph로 N+1 제거
* **인증**: Spring Security + JWT (닉네임 포함)
* **관리자(Admin)**: 역할 변경 시 AOP 선(先) 로깅
* **로그(Log)**: 매니저 등록과 독립적 저장(`@Transactional(REQUIRES_NEW)`)

---

## 레벨별 핵심 해결
* **@Transactional**: 클래스 전체 readOnly → 쓰기 메서드에 메서드 단위 `@Transactional`로 오버라이드
* **JWT**: `User.nickname` 추가 후 토큰 클레임에 포함
* **JPQL 검색**: `weather`·`modifiedAt` 기간 조건 동적 처리
* **테스트**: 존재하지 않는 Todo 조회 시 `404` 검증
* **AOP**: `@Before`로 `changeUserRole()` 실행 전 로깅
* **Cascade**: 할 일 생성 시 작성자를 자동 담당자로 등록
* **N+1 문제**: 댓글/유저 조회 시 Fetch Join·배치 사이즈 적용
* **QueryDSL 변환**: `findByIdWithUser`를 QueryDSL로 구현하며 N+1 방지
* **Spring Security**: 기존 필터/리졸버 제거, `@PreAuthorize`와 JWT 기반 인증 유지
* **QueryDSL 검색 API**: 제목·생성일·담당자 닉네임 조건, 담당자·댓글 수 집계, 최신순 정렬, 페이징
* **Transaction 심화**: 매니저 등록 실패와 관계없이 로그는 별도 트랜잭션으로 저장

---

## 커밋 메시지 예시

* `fix(tx): write 메서드 readOnly 오버라이드`
* `feat(security): Spring Security + JWT 적용`
* `perf(querydsl): 검색 Projections 및 group by`
* `fix(aop): changeUserRole 실행 전 로깅`
* `test(controller): todo not-found 404 검증`

---

## 폴더 구조 (요약)

```
src/main/java/org/example/expert
 ├─ domain/{auth, comment, common, log, manager, todo, user}
 ├─ global/security,jwt,aop,GlobalExceptionHandler
 └─ common/dto,util
```
