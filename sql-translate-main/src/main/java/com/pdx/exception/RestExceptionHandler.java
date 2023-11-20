package com.pdx.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/*
 * @Author IT派同学
 * @Description 异常处理
 * @Date 2023/7/24
 **/
@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public Result exception(Exception e){
        log.error("Exception====>{}",e.getLocalizedMessage(),e);
        return Result.fail(ResponseCode.FAIL);
    }

    @ExceptionHandler(value = BusinessException.class)
    public Result businessException(BusinessException e){
        log.error("businessException ====>{}",e.getLocalizedMessage(),e);
        return Result.fail(e.getCode(),e.getMessage());
    }


    /**
     * 框架异常
     * @param e
     * @param <T>
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    <T> Result<T> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {

        log.error("methodArgumentNotValidExceptionHandler bindingResult.allErrors():{}, exception:{}", e.getBindingResult().getAllErrors(), e);
        List<ObjectError> errors = e.getBindingResult().getAllErrors();
        return createValidExceptionResp(errors);
    }

    private <T> Result<T> createValidExceptionResp(List<ObjectError> errors) {
        String[] msgs = new String[errors.size()];
        int i = 0;
        for (ObjectError error : errors) {
            msgs[i] = error.getDefaultMessage();
            log.info("msg={}",msgs[i]);
            i++;
        }
        return Result.fail(ResponseCode.FAIL.getCode(), msgs[0]);
    }

    @ExceptionHandler(RuntimeException.class)
    public Result runtimeExceptionHandler (RuntimeException e) {
        log.error("runtimeExceptionHandler ====> {}, {}", e.getLocalizedMessage(), e);
        return Result.fail(ResponseCode.FAIL.getCode(), ResponseCode.FAIL.getMessage());
    }
}