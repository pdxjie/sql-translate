package com.pdx.exception;

import lombok.Data;
import lombok.Getter;

/*
 * @Author IT 派同学
 * @Description 业务异常处理
 * @Date 2023/7/24
 **/
@Data
@Getter
public class BusinessException extends RuntimeException{

    // 错误码 ❌
    private Integer code;

    // 错误信息 ❎
    private String message;

    public BusinessException (ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }

    public BusinessException (Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException (String message) {
        this.code = ResponseCode.FAIL.getCode();
        this.message = message;
    }

    public BusinessException () {
        this.code = ResponseCode.FAIL.getCode();
        this.message =ResponseCode.FAIL.getMessage();
    }
}