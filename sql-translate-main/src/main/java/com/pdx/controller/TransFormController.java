package com.pdx.controller;

import com.pdx.entity.TransFrom;
import com.pdx.res.DataResult;
import com.pdx.service.TransFromService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: GCJ
 * @Date: 2022/9/20 15:08:26
 * @Description: 转换前端控制器
 */
@CrossOrigin
@RestController
@RequestMapping("/")
@Slf4j
public class TransFormController {

    @Autowired
    private TransFromService transFromService;

    @PostMapping("/transform")
    public DataResult doTransForm(@RequestBody(required = false)TransFrom transFrom) throws Exception {
        if (StringUtils.isEmpty(transFrom.getTextarea())){
            return DataResult.getResult(100,"请补充需要转译的JSON语句块",null);
        }
        DataResult dataResult = transFromService.transformSql(transFrom);
        return dataResult;
    }
}
