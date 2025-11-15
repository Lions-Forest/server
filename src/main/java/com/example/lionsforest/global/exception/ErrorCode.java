package com.example.lionsforest.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //400 BAD_REQUEST
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "INVALID_PARAMETER", "파라미터가 유효하지 않습니다."),

    // 401 UNAUTHORIZED
    INVALID_ID_TOKEN(HttpStatus.UNAUTHORIZED, "INVALID_ID_TOKEN", "유효하지 않은 토큰입니다."),

    // 403 FORBIDDEN
    USER_NOT_IN_WHITELIST(HttpStatus.FORBIDDEN, "USER_NOT_IN_WHITELIST", "동아리 부원 명단에 존재하지 않습니다."),

    // 404 NOT_FOUND
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),

    // 409 CONFLICT
    NICKNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, "NICKNAME_ALREADY_EXISTS", "이미 사용 중인 닉네임입니다."),

    // 닉네임 생성 실패
    NICKNAME_GENERATION_FAILED(HttpStatus.NO_CONTENT, "NICKNAME_GENERATION_FAILED", "새로운 닉네임 생성에 실패했습니다."),

    //댓글 조회 실패
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT_NOT_FOUND", "댓글 조회에 실패했습니다"),

    //모임 없음
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "모임 조회에 실패했습니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
