package com.sparta.hanghaememo.dto.exception;

import com.sparta.hanghaememo.entity.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {

    private int statusCode;
    private String error;
    private String msg;

    public ErrorResponseDto(ErrorCode errorCode) {
        this.statusCode = errorCode.getStatus().value();
        this.error = errorCode.getStatus().name();
        this.msg = errorCode.getMessage();
    }

}
