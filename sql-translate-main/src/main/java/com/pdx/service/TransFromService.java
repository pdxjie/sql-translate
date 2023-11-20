package com.pdx.service;

import com.pdx.exception.Result;
import com.pdx.model.vo.TransFromVo;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: JSON 转换 SQL 接口服务
 */
public interface TransFromService {

    Result<?> transformSql(TransFromVo transFrom) throws Exception;
}
