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
    GROUP_NOT_FOUND(HttpStatus.NOT_FOUND, "GROUP_NOT_FOUND", "모임 조회에 실패했습니다"),

    // 권한 없음
    GROUP_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "GROUP_PERMISSION_DENIED", "해당 모임에 대한 권한이 없습니다."),

    // 모임 취소 시점 제한
    GROUP_CANCEL_TIME_EXCEEDED(HttpStatus.BAD_REQUEST, "GROUP_CANCEL_TIME_EXCEEDED", "모임 시작 이후에는 취소할 수 없습니다."),

    // 모임 중복 신청 제한
    PARTICIPATION_ALREADY_EXISTS(HttpStatus.CONFLICT, "PARTICIPATION_ALREADY_EXISTS", "이미 참여 중인 모임입니다."),

    // 모임 인원 제한
    GROUP_CAPACITY_FULL(HttpStatus.BAD_REQUEST, "GROUP_CAPACITY_FULL", "모임 인원이 가득 찼습니다."),

    // 모임장은 탈퇴할 수 없음
    GROUP_LEADER_CANNOT_LEAVE(HttpStatus.FORBIDDEN, "GROUP_LEADER_CANNOT_LEAVE", "모임장은 모임을 탈퇴할 수 없습니다."),

    // 참여하지 않은 모임
    GROUP_PARTICIPATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "GROUP_PARTICIPATION_NOT_FOUND", "참여하지 않은 모임입니다."),

    // 권한 없음
    COMMENT_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "COMMENT_PERMISSION_DENIED", "해당 댓글에 대한 권한이 없습니다."),

    // 권한 없음
    REVIEW_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "REVIEW_PERMISSION_DENIED", "해당 후기에 대한 권한이 없습니다."),

    // 후기 없음
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "REVIEW_NOT_FOUND", "후기 조회에 실패했습니다");


    private final HttpStatus status;
    private final String code;
    private final String message;
}
