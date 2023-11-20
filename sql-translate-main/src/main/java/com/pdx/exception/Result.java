package com.pdx.exception;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;

/*
 * @Author IT派同学
 * @Description 响应结果
 * @Date 2023/7/24
 **/
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
public class Result<T> implements Serializable {

    // 响应状态码
    private Integer code;

    // 响应信息
    private String message;

    // 数据
    private T data;

    private Result (Integer code) {
        this.code = code;
    }

    private Result (Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private Result (Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @JsonIgnore
    @JSONField(serialize = false)
    public boolean isSuccess () {
        return Objects.equals(this.code, ResponseCode.SUCCESS.getCode());
    }

    // 成功响应
    public static <T>Result<T> success () {
        return new Result<T>(ResponseCode.SUCCESS.getCode());
    }

    public static <T>Result<T> success (String message) {
        return new Result<T>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T>Result<T> success (T data) {
        return new Result<T>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    // 失败响应
    public static <T>Result<T> fail () {
        return new Result<T>(ResponseCode.FAIL.getCode());
    }

    public static <T>Result<T> fail (T data) {
        return new Result<T>(ResponseCode.FAIL.getCode(), ResponseCode.FAIL.getMessage(), data);
    }

    public static <T>Result<T> fail (String message) {
        return new Result<T>(ResponseCode.FAIL.getCode(), message);
    }

    public static <T>Result<T> fail (Integer code, String message) {
        return new Result<T>(code, message);
    }

    public static <T>Result<T> fail (ResponseCode responseCode) {
        return new Result<T>(responseCode.getCode(), responseCode.getMessage());
    }
}
