package com.pdx.controller;

import com.pdx.exception.ResponseCode;
import com.pdx.exception.Result;
import com.pdx.model.vo.TransFromVo;
import com.pdx.service.TransFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description:
 */
@CrossOrigin
@RestController
@RequestMapping("v1")
public class TransFormController {

    @Autowired
    private TransFromService transFromService;

    @PostMapping("/transform")
    public Result<?> doTransForm(@RequestBody(required = false) TransFromVo transFrom) throws Exception {
        if (StringUtils.isEmpty(transFrom.getTextarea())){
            return Result.fail(ResponseCode.PLEASE_SUPPLEMENT_NEED_TRANSFORM_DATA);
        }
        return transFromService.transformSql(transFrom);
    }
}
