package com.pdx.common;

import lombok.Data;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: 公共响应类
 */
@Data
public class DataResult<T> {
    //code值
    private int code;
    //信息
    private String msg;
    //响应体
    private T data;

    public DataResult(int code,T data){
        this.code = code;
        this.data = data;
        this.msg = null;
    }

    public DataResult(int code,String msg,T data){
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public DataResult() {

    }

    /**
     * 自定义返回操作
     * @param code
     * @param msg
     * @param data
     * @param <T>
     * @return
     */
    public static <T>DataResult getResult(int code,String msg,T data){
        return new DataResult(code,msg,data);
    }

    /**
     * 自定义返回，data为null
     * @param code
     * @param msg
     * @return
     */
    public static DataResult getResult(int code,String msg){
        return new DataResult(code,msg);
    }

}
