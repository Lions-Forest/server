package com.example.lionsforest.global.exception;

import lombok.Getter;

//GlobalExceptionHandler가 @ExceptionHandler(BusinessException.class)
//하나로 모든 비즈니스 예외를 처리할 수 있도록 BusinessException 클래스 사용
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage()); //부모 생성자에 메시지 전달
        this.errorCode = errorCode;
    }

    //근본 원인이 되는 예외를 함께 전달받는 생성자(필요시 사용)
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}
