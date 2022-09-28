package com.pdx.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: GCJ
 * @Date: 2022/9/20 15:09:36
 * @Description: 响应类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {

    private boolean success;
    private Object response;
    private String message;

    public Result(Object response) {
        this.response = response;
    }
}
