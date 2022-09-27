package com.pdx.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: PDX
 * @Date: 2022/9/20 15:07:55
 * @Description: 转换类
 */
@Data
public class TransFrom implements Serializable {

    private String textarea;

    private String type;

    private List<ReplaceForm> domains;


}
