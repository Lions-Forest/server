package com.example.lionsforest.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice //모든 @RestController에서 발생하는 예외를 처리함
public class GlobalExceptionHandler {
    //우리가 정의한 BusinessException을 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("handleBusinessException: {}", e.getMessage());

        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());

        //ErrorCode에서 정의한 HttpStatus, ErrorResponse DTO를 반환
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 나머지 예상치 못한 예외들(500 에러)을 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("handleException : {}", e.getMessage());

        // 일단 500 에러로 처리
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // ErrorResponse DTO (내부 클래스 또는 별도 파일로 정의)
    public record ErrorResponse(String code, String message) {
    }
}
