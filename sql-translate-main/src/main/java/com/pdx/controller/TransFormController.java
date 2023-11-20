package com.pdx.controller;

import com.pdx.model.vo.TransFromVo;
import com.pdx.common.DataResult;
import com.pdx.service.TransFromService;
import com.pdx.service.impl.TransFromServiceImpl;
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
@RequestMapping("v1")
public class TransFormController {

    @Autowired
    private TransFromService transFromService;

    @PostMapping("/transform")
    public DataResult doTransForm(@RequestBody(required = false) TransFromVo transFrom) throws Exception {
        if (StringUtils.isEmpty(transFrom.getTextarea())){
            return DataResult.getResult(100,"请补充需要转译的JSON语句块",null);
        }
        return transFromService.transformSql(transFrom);
    }
}
