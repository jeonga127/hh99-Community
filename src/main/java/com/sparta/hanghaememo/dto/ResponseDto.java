package com.sparta.hanghaememo.dto;

import com.sparta.hanghaememo.entity.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "add")
public class ResponseDto<T> {
    private String message;
    private StatusEnum status;
    private T data;


    public static <T> ResponseDto<T> setSuccess(String message, T data){
        return ResponseDto.add(message, StatusEnum.OK , data);
    }

    public static <T> ResponseDto<T> setFail(String message){
        return ResponseDto.add(message, StatusEnum.BAD_REQUEST, null);
    }
}
