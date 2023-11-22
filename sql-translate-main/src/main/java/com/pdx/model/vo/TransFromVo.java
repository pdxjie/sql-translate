package com.pdx.model.vo;

import lombok.Data;
import java.util.List;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: 转译内容请求体
 */
@Data
public class TransFromVo {

    private String textarea;

    private String type;

    private List<ReplaceFieldVo> domains;
}
