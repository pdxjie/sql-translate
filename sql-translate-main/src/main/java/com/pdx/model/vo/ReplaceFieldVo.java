package com.pdx.model.vo;

import lombok.Data;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: 字段替换请求体
 */
@Data
public class ReplaceFieldVo {

    private String oldVal;

    private String newVal;
}
