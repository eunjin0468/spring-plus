package org.example.expert.domain.manager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.log.service.LogService;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.entity.Manager;
import org.example.expert.domain.manager.repository.ManagerRepository;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.example.expert.domain.log.enums.LogAction.MANAGER_ASSIGN;
import static org.example.expert.domain.log.enums.LogAction.MANAGER_DELETE;
import static org.example.expert.domain.log.enums.LogStatus.FAIL;
import static org.example.expert.domain.log.enums.LogStatus.SUCCESS;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final ManagerRepository managerRepository;
    private final UserRepository userRepository;
    private final TodoRepository todoRepository;

    private final LogService logService;
    private final ObjectMapper objectMapper;

    @Transactional
    public ManagerSaveResponse saveManager(AuthUser authUser, long todoId, ManagerSaveRequest managerSaveRequest) {
        User requester = User.fromAuthUser(authUser);
        Long requesterId = requester.getId();
        String payload = toJsonSafe(Map.of(
                "todoId", todoId,
                "managerUserId", managerSaveRequest.getManagerUserId(),
                "requesterId", requesterId
        ));

        try {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new InvalidRequestException("Todo not found"));

            if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(requesterId, todo.getUser().getId())) {
                throw new InvalidRequestException("담당자를 등록하려고 하는 유저가 유효하지 않거나, 일정을 만든 유저가 아닙니다.");
            }

            User managerUser = userRepository.findById(managerSaveRequest.getManagerUserId())
                    .orElseThrow(() -> new InvalidRequestException("등록하려고 하는 담당자 유저가 존재하지 않습니다."));

            if (ObjectUtils.nullSafeEquals(requesterId, managerUser.getId())) {
                throw new InvalidRequestException("일정 작성자는 본인을 담당자로 등록할 수 없습니다.");
            }

            Manager newManagerUser = new Manager(managerUser, todo);
            Manager savedManagerUser = managerRepository.save(newManagerUser);

            // 성공 로그
            safeLog(() -> logService.write(
                    MANAGER_ASSIGN,          // action
                    SUCCESS,                 // status
                    requesterId,               // requesterId
                    savedManagerUser.getId(),  // targetId (생성된 manager row id)
                    "manager assigned",        // message
                    payload                    // payload(JSON)
            ));

            return new ManagerSaveResponse(
                    savedManagerUser.getId(),
                    new UserResponse(managerUser.getId(), managerUser.getEmail(), managerUser.getNickname())
            );
        } catch (Exception e) {
            // 실패 로그 (업무 롤백돼도 로그는 커밋)
            safeLog(() -> logService.write(
                    MANAGER_ASSIGN,
                    FAIL,
                    requesterId,
                    null, // 실패 시 targetId가 없을 수 있음
                    e.getMessage(),
                    payload
            ));
            throw e; // 비즈니스 트랜잭션은 롤백되지만 위 로그는 이미 커밋됨
        }

    }

    public List<ManagerResponse> getManagers(AuthUser authUser, long todoId) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new InvalidRequestException("Todo not found"));

        List<Manager> managerList = managerRepository.findByTodoIdWithUser(todo.getId());

        List<ManagerResponse> dtoList = new ArrayList<>();
        for (Manager manager : managerList) {
            User user = manager.getUser();
            dtoList.add(new ManagerResponse(
                    manager.getId(),
                    new UserResponse(user.getId(), user.getEmail(), user.getNickname())
            ));
        }
        return dtoList;
    }

    @Transactional
    public void deleteManager(AuthUser authUser, long todoId, long managerId) {
        User requester = User.fromAuthUser(authUser);
        Long requesterId = requester.getId();
        String payload = toJsonSafe(Map.of(
                "todoId", todoId,
                "managerId", managerId,
                "requesterId", requesterId
        ));

        try {
            Todo todo = todoRepository.findById(todoId)
                    .orElseThrow(() -> new InvalidRequestException("Todo not found"));

            if (todo.getUser() == null || !ObjectUtils.nullSafeEquals(requesterId, todo.getUser().getId())) {
                throw new InvalidRequestException("해당 일정을 만든 유저가 유효하지 않습니다.");
            }

            Manager manager = managerRepository.findById(managerId)
                    .orElseThrow(() -> new InvalidRequestException("Manager not found"));

            if (!ObjectUtils.nullSafeEquals(todo.getId(), manager.getTodo().getId())) {
                throw new InvalidRequestException("해당 일정에 등록된 담당자가 아닙니다.");
            }

            managerRepository.delete(manager);

            safeLog(() -> logService.write(
                    MANAGER_DELETE,
                    SUCCESS,
                    requesterId,
                    managerId,
                    "manager deleted",
                    payload
            ));

        } catch (Exception e) {
            safeLog(() -> logService.write(
                    MANAGER_DELETE,
                    FAIL,
                    requesterId,
                    managerId,
                    e.getMessage(),
                    payload
            ));
            throw e;
        }
    }

    private String toJsonSafe(Object o) {
        try { return objectMapper.writeValueAsString(o); }
        catch (Exception e) { return "{\"_payload\":\"serialization_failed\"}"; }
    }

    private void safeLog(Runnable r) {
        try { r.run(); } catch (Exception ignored) {}
    }

}
