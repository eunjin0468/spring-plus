package org.example.expert.domain.log.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum LogAction {
    MANAGER_REGISTER,
    MANAGER_ASSIGN, // 담당자 등록
    MANAGER_DELETE // 담당자 삭제
}
