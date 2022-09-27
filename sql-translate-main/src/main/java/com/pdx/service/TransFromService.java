package com.pdx.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pdx.entity.ReplaceForm;
import com.pdx.entity.TransFrom;
import com.pdx.res.DataResult;
import com.pdx.utils.TranslationOperate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 派 大 星
 * @function
 * @date 2022/9/20 22:44
 * @website https://blog.csdn.net/Gaowumao
 */
@Service
@Slf4j
public class TransFromService {

    /**
     * @param transFrom
     * @return
     */
    public DataResult transformSql(TransFrom transFrom) throws Exception {
        Map<String,Object> result = new HashMap<>();
        if (StringUtils.isEmpty(transFrom.getType())){
            return DataResult.getResult(101,"请选择需要转译类型",null);
        }
        DataResult dataResult = new DataResult();
        if ("1".equals(transFrom.getType())){
            dataResult = getInsertSql(transFrom.getTextarea(), transFrom.getDomains());
        }else if ("2".equals(transFrom.getType())){
            dataResult = getUpdateSql(transFrom.getTextarea(), transFrom.getDomains());
        }else if ("3".equals(transFrom.getType())){
            dataResult = getCreateSql(transFrom.getTextarea(),transFrom.getDomains());
        }
        return dataResult;
    }
    /**
     * 转译创建语句
     * @param textarea
     * @param replaceForms
     * @return
     */
    private DataResult getCreateSql(String textarea, List<ReplaceForm> replaceForms) {
        int index = 0;
        String createTable = "create table ";
        JSONObject jsonObject = JSONObject.parseObject(textarea);
        if (replaceForms.size() == 0 || "".equals(replaceForms.get(0).getNewVal())){
            //判断是否存在表名
            if (!jsonObject.keySet().contains("@table")){
                //throw new BusinessException(BaseResponseCode.NOT_EXISTS_TABLE_NAME);
                return DataResult.getResult(102,"请设置表名称",null);
            }else {
                createTable+="`"+jsonObject.get("@table").toString()+"`(";
                //移除表名
                jsonObject.remove("@table");
                //用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                int count = jsonObject.keySet().size();
                for (String key : jsonObject.keySet()) {
                    index+=1;
                    if (!key.contains("@")){
                        return DataResult.getResult(103,"缺少字段长度",null);
                    }
                    String[] split = key.split("@");

                    if (split.length == 1 || StringUtils.isEmpty(split[1]) || " ".equals(split[1])){
                        return DataResult.getResult(103,"缺少字段长度",null);
                    }else if (!split[1].matches("[0-9]+")){
                        return DataResult.getResult(103,"字段长度必须为整数类型",null);
                    }
                    String value="";
                    if (!"children".equals(key.split("@")[0])){
                        value = (String) jsonObject.get(key);
                    }
                    //判断key中是否存在id属性，默认id属性为主键
                    //如果是数字
                    if (value.matches("[0-9]+") && value.length() <=2){
                        //新增children start
                        if ((index+1) - count == 1){
                            if ("children".equals(split[0])){
                                createTable+="`pid`"+" varchar("+split[1]+") ";
                            }
                        }else {
                            if ("children".equals(split[0])){
                                createTable+="`pid`"+" varchar("+split[1]+"), ";
                            }
                        }
                        //新增children  end
                        if ((index+1) - count == 1){
                            if ("id".equals(split[0])){
                                createTable+="`"+split[0]+"`"+" int("+split[1]+") primary key";
                            }else {
                                createTable+="`"+split[0]+"`"+" int ("+split[1]+")";
                            }
                        }else {
                            if ("id".equals(split[0])){
                                createTable+="`"+split[0]+"`"+" int("+split[1]+") primary key,";
                            }else {
                                createTable+="`"+split[0]+"`"+" int("+split[1]+"),";
                            }
                        }
                    }else {
                        //新增children start
                        if ((index+1) - count == 1){
                            if ("children".equals(split[0])){
                                createTable+="`pid`"+" varchar("+split[1]+") ";
                            }
                        }else {
                            if ("children".equals(split[0])){
                                createTable+="`pid`"+" varchar("+split[1]+"), ";
                            }
                        }
                        //新增children  end
                        if ((index+1) - count == 1){
                            if ("id".equals(split[0])){
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+") primary key";
                            }else {
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+")";
                            }
                        }else {
                            if ("id".equals(split[0])){
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+") primary key,";
                            }else {
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+"),";
                            }
                        }
                    }
                }
                createTable+=");";
            }
        }else {
            Map<String,String> replaceMap = new HashMap<>();
            replaceForms.stream().forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(),replaceForm.getNewVal());
            });
            //children不能修改
            if (replaceMap.keySet().contains("children")){
                return DataResult.getResult(102,"默认属性不能修改",null);
            }
            //判断是否存在表名
            if (!jsonObject.keySet().contains("@table")) {
                return DataResult.getResult(102,"请设置表名称",null);
            } else {
                createTable += "`" + jsonObject.get("@table").toString() + "`(";
                //移除表名
                jsonObject.remove("@table");
                //用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                int count = jsonObject.keySet().size();
                for (String key : jsonObject.keySet()) {
                    index+=1;
                    //判断是否设置字段长度
                    if (!key.contains("@")){
                        return DataResult.getResult(103,"缺少字段长度",null);
                    }
                    String[] split = key.split("@");
                    if (split.length == 1 || StringUtils.isEmpty(split[1]) || " ".equals(split[1])){
                        return DataResult.getResult(103,"缺少字段长度",null);
                    }else if (!split[1].matches("[0-9]+")){
                        return DataResult.getResult(103,"字段长度必须为整数类型",null);
                    }
                    String value="";
                    if (!"children".equals(key.split("@")[0])){
                        value = (String) jsonObject.get(key);
                    }
                    if (replaceMap.keySet().contains(split[0])){
                        String newKey = replaceMap.get(split[0]);
                        if (value.matches("[0-9]+") && value.length() <=2){
                            //新增children start
                            if ((index+1) - count == 1){
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+") ";
                                }
                            }else {
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+"), ";
                                }
                            }
                            //新增children  end
                            if ((index+1) - count == 1){
                                createTable+="`"+newKey+"`"+" int("+split[1]+")";
                            }else {
                                createTable+="`"+newKey+"`"+" int("+split[1]+"),";
                            }
                        }else {

                            //新增children start
                            if ((index+1) - count == 1){
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+") ";
                                }
                            }else {
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+"), ";
                                }
                            }
                            //新增children  end

                            if ((index+1) - count == 1){
                                createTable+="`"+newKey+"`"+" varchar("+split[1]+")";
                            }else {
                                createTable+="`"+newKey+"`"+" varchar("+split[1]+"),";
                            }
                        }
                    }else {
                        if (value.matches("[0-9]+") && value.length() <=2){
                            //新增children start
                            if ((index+1) - count == 1){
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+") ";
                                }
                            }else {
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+"), ";
                                }
                            }
                            //新增children  end


                            if ((index+1) - count == 1){
                                createTable+="`"+split[0]+"`"+" int("+split[1]+")";
                            }else {
                                createTable+="`"+split[0]+"`"+"int("+split[1]+"),";
                            }
                        }else {

                            //新增children start
                            if ((index+1) - count == 1){
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+") ";
                                }
                            }else {
                                if ("children".equals(split[0])){
                                    createTable+="`pid`"+" varchar("+split[1]+"), ";
                                }
                            }
                            //新增children  end

                            if ((index+1) - count == 1){
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+")";
                            }else {
                                createTable+="`"+split[0]+"`"+" varchar("+split[1]+"),";
                            }
                        }
                    }
                }
                createTable+=");";
            }
        }
        return DataResult.getResult(0,"",createTable);
    }
    /**
     * 转译更新语句
     * @param textarea
     * @param replaceForms
     * @return
     */
    private DataResult getUpdateSql(String textarea, List<ReplaceForm> replaceForms) {
        Integer index = 0;
        String updateSql = "update ";
        //保存where条件的属性和值
        Map<String,String> conditionMap = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(textarea);
        //判断是否存在替换属性
        if (replaceForms.size() == 0 || "".equals(replaceForms.get(0).getNewVal())){
            if (!jsonObject.keySet().contains("@table")){
                return DataResult.getResult(102,"请设置表名称",null);
            }else {
                updateSql+="`"+jsonObject.get("@table").toString()+"` ";
                //移除表名
                jsonObject.remove("@table");
                //判断是否存在更新条件
                List<String> condition = jsonObject.keySet().stream().filter(key ->
                        key.contains("#")
                ).collect(Collectors.toList());
                //保存条件
                for (String key : condition) {
                    conditionMap.put(key,jsonObject.get(key).toString());
                }
                if (condition.size() == 0){
                    return DataResult.getResult(102,"请指定where子句后面的更新条件",null);
                }else {
                    for (String con : condition) {
                        jsonObject.remove(con);
                    }
                    //用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                    int count = jsonObject.keySet().size();
                    for (String key : jsonObject.keySet()) {
                        index+=1;
                        if ((index+1) - count == 1){
                            updateSql+=" `"+key+"` ="+"\'"+jsonObject.get(key).toString()+"\' where ";
                        }else {
                            if (index == 1){
                                updateSql+="set `"+key+"` ="+"\'"+jsonObject.get(key).toString()+"\',";
                            }else {
                                updateSql+=" `"+key+"` ="+"\'"+jsonObject.get(key).toString()+"\',";
                            }

                        }
                    }
                }
            }
        }else {
            //替换属性
            Map<String,String> replaceMap = new HashMap<>();
            replaceForms.stream().forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(),replaceForm.getNewVal());
            });
            //是否存在表名
            if (!jsonObject.keySet().contains("@table")){
                return DataResult.getResult(102,"请设置表名称",null);
            }else {
                updateSql+="`"+jsonObject.get("@table").toString()+"` ";
                //移除表名
                jsonObject.remove("@table");
                //替换属性
                JSONObject result = new JSONObject();
                for (String key : jsonObject.keySet()) {
                    for (String Jkey : replaceMap.keySet()) {
                        //判断修改字段是否为条件字段
                        if (key.contains("#")){
                            String[] split = key.split("#");
                            if (split[0].equals(Jkey)){
                                result.put(split[0].replace(Jkey, replaceMap.get(Jkey))+"#"+split[1],jsonObject.get(key));
                                break;
                            }else {
                                result.put(key,jsonObject.get(key));
                                break;
                            }
                        }else {
                            if (key.equals(Jkey)){
                                //key = key.replace(Jkey, replaceMap.get(Jkey));
                                result.put(key.replace(Jkey, replaceMap.get(Jkey)),jsonObject.get(key));
                                break;
                            }else {
                                if (!replaceMap.keySet().contains(key)){
                                    result.put(key,jsonObject.get(key));
                                }
                            }
                        }
                    }
                }
                //判断是否存在更新条件
                List<String> condition = result.keySet().stream().filter(key ->
                        key.contains("#")
                ).collect(Collectors.toList());
                //保存条件
                for (String key : condition) {
                    conditionMap.put(key,result.getString(key));
                }
                if (condition.size() == 0){
                    return DataResult.getResult(102,"请指定where子句后面的更新条件",null);
                }else {
                    for (String con : condition) {
                        result.remove(con);
                    }
                    //用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                    int count = result.keySet().size();
                    for (String key : result.keySet()) {
                        index+=1;
                        if (replaceMap.keySet().contains(key)){
                            String newKey = replaceMap.get(key);
                            if ((index+1) - count == 1){
                                updateSql+=" "+newKey+"="+result.get(key).toString()+" where ";
                            }else {
                                if (index == 1){
                                    updateSql+="set `"+newKey+"` ="+"\'"+result.get(key).toString()+"\',";
                                }else {
                                    updateSql+=" `"+newKey+"` ="+"\'"+result.get(key).toString()+"\',";
                                }
                            }
                        }else {
                            if ((index+1) - count == 1){
                                updateSql+=" `"+key+"` ="+"\'"+result.get(key).toString()+"\' where ";
                            }else {
                                if (index == 1){
                                    updateSql+="set `"+key+"` ="+"\'"+result.get(key).toString()+"\',";
                                }else {
                                    updateSql+=" `"+key+"` ="+"\'"+result.get(key).toString()+"\',";
                                }
                            }
                        }
                    }
                }
            }
        }
        index = 0;
        int size = conditionMap.keySet().size();
        for (String key : conditionMap.keySet()) {
            String value = conditionMap.get(key);
            index +=1;
            String[] condition = key.split("#");
            if ((index+1) - size == 1){
                updateSql+="`"+condition[0]+"` "+condition[1]+" "+"\'"+value+"\';";
            }else {
                updateSql+="`"+condition[0]+"` "+condition[1]+" "+"\'"+value+"\' and ";
            }

        }
        return DataResult.getResult(0,"",updateSql);
    }

    /**
     * 转译插入语句 需要指定pid
     * @param textarea
     * @param replaceForms
     * @return
     */
    private DataResult getInsertSql(String textarea, List<ReplaceForm> replaceForms) throws Exception {
         String insertSql = "insert into ";
         Integer index = 0;
        //先判断是JsonArray还是JsonObject
        if (!textarea.startsWith("[")){//说明是JsonObject
            JSONObject jsonObject = JSONObject.parseObject(textarea);
            //判断是否存在表
            if (jsonObject.keySet().contains("@table")){
                insertSql+= "`"+jsonObject.get("@table")+"` (";
            }else {
                return DataResult.getResult(102,"请设置表名称",null);
            }
            //移除@table
            jsonObject.remove("@table");
            //没有需要替换的属性
            // || 后面的含义是有空值传入
            if (replaceForms.size() == 0 || "".equals(replaceForms.get(0).getNewVal())){
                //判断是否存在多级嵌套
                if (jsonObject.keySet().contains("children")){
                    //如果children数组中没有值，添加了@pid直接去掉
                    if ("[]".equals(jsonObject.get("children").toString())){//存在children，但children数组中不存在值
                        jsonObject.remove("children");
                        if (jsonObject.keySet().contains("@pid")){
                            //如果存在则移除
                            jsonObject.remove("@pid");
                        }
                        int count = jsonObject.keySet().size();
                        //设置insert语句所插入字段
                        for (String key : jsonObject.keySet()) {
                            index+=1;
                            //最后一个属性
                            if ((index+1) - count == 1){
                                insertSql+="`"+key+"` ) values (";
                                index = 0;
                            }else {
                                insertSql+="`"+key+"` ,";
                            }
                        }
                        //设置插入的值
                        for (String key : jsonObject.keySet()) {
                            index+=1;
                            //获取value
                            String value = (String) jsonObject.get(key);
                            //判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index+1) - count == 1){
                                insertSql+="\'"+value+"\' );";
                            }else {
                                insertSql+="\'"+value+"\' ,";
                            }
                        }
                    }else {
                        if (jsonObject.keySet().contains("@pid")){
                            JSONArray resultArray = new JSONArray();
                            //resultArray = (JSONArray) jsonObject.get("children");
                            String children = jsonObject.get("children").toString();
                            resultArray = JSONArray.parseArray(children);
                            jsonObject.remove("children");
                            LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                            //将key存入linkedHashMap中保证插入字段的顺序
                            for (String key : jsonObject.keySet()) {
                                if (key.equals("@pid")){
                                    linkedHashMap.put(key.replace("@",""),"0");
                                }else{
                                    linkedHashMap.put(key,jsonObject.getString(key));
                                }
                            }
                            int count = linkedHashMap.size();
                            index = 0;
                            //设置insert语句所插入字段
                            for (String key : linkedHashMap.keySet()) {
                                index+=1;
                                //最后一个属性
                                if ((index+1) - count == 1){
                                    insertSql+="`"+key+"` ) values (";
                                    index = 0;
                                }else {
                                    insertSql+="`"+key+"` ,";
                                }
                            }
                            //设置插入的值
                            index = 0;
                            for (String key : linkedHashMap.keySet()) {
                                index+=1;
                                //获取value
                                String value = (String) linkedHashMap.get(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index+1) - count == 1){
                                    insertSql+="\'"+value+"\' ),";
                                }else {
                                    insertSql+="\'"+value+"\' ,";
                                }
                            }
                            insertSql+= TranslationOperate.getChildrenSql(jsonObject.getString(jsonObject.getString("@pid")),resultArray,linkedHashMap,jsonObject.getString("@pid"));
                        }else {
                            return DataResult.getResult(103,"请在该JSON块中选取一个key值作为pid",null);
                        }
                    }
                }else {
                    int count = jsonObject.keySet().size();
                    //设置insert语句所插入字段
                    for (String key : jsonObject.keySet()) {
                        index+=1;
                        //最后一个属性
                        if ((index+1) - count == 1){
                            insertSql+="`"+key+"` ) values (";
                            index = 0;
                        }else {
                            insertSql+="`"+key+"` ,";
                        }
                    }
                    //设置插入的值
                    for (String key : jsonObject.keySet()) {
                        index+=1;
                        //获取value
                        String value = (String) jsonObject.get(key);
                        //判断是否是最后一个属性，最后一个属性不加逗号
                        if ((index+1) - count == 1){
                            insertSql+="\'"+value+"\' );";
                        }else {
                            insertSql+="\'"+value+"\' ,";
                        }
                    }
                }
            }else {//有替换值
                Map<String,String> replaceMap = new HashMap<>();
                replaceForms.stream().forEach(replaceForm -> {
                    replaceMap.put(replaceForm.getOldVal(),replaceForm.getNewVal());
                });
                if (replaceMap.keySet().contains("children") || replaceMap.keySet().contains("@pid")){
                    return DataResult.getResult(103,"默认属性不允许被替换！",null);
                }else {
                    //递归替换属性
                    jsonObject = TranslationOperate.replaceFields(replaceMap,jsonObject);
                    //判断是否存在多级嵌套
                    if (jsonObject.keySet().contains("children")){
                        //如果children数组中没有值，添加了@pid直接去掉
                        if ("[]".equals(jsonObject.get("children").toString())){//存在children，但children数组中不存在值
                            jsonObject.remove("children");
                            if (jsonObject.keySet().contains("@pid")){
                                //如果存在则移除
                                jsonObject.remove("@pid");
                            }
                            int count = jsonObject.keySet().size();
                            //设置insert语句所插入字段
                            for (String key : jsonObject.keySet()) {
                                index+=1;
                                //最后一个属性
                                if ((index+1) - count == 1){
                                    insertSql+="`"+key+"` ) values (";
                                    index = 0;
                                }else {
                                    insertSql+="`"+key+"` ,";
                                }
                            }
                            //设置插入的值
                            for (String key : jsonObject.keySet()) {
                                index+=1;
                                //获取value
                                String value = (String) jsonObject.get(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index+1) - count == 1){
                                    insertSql+="\'"+value+"\' );";
                                }else {
                                    insertSql+="\'"+value+"\' ,";
                                }
                            }
                        }else {
                            if (jsonObject.keySet().contains("@pid")){
                                JSONArray resultArray = new JSONArray();
                                //resultArray = (JSONArray) jsonObject.get("children");
                                String children = jsonObject.get("children").toString();
                                resultArray = JSONArray.parseArray(children);
                                jsonObject.remove("children");
                                LinkedHashMap<String,String> linkedHashMap = new LinkedHashMap<>();
                                //将key存入linkedHashMap中保证插入字段的顺序
                                for (String key : jsonObject.keySet()) {
                                    if (key.equals("@pid")){
                                        linkedHashMap.put(key.replace("@",""),"0");
                                    }else{
                                        linkedHashMap.put(key,jsonObject.getString(key));
                                    }
                                }
                                int count = linkedHashMap.size();
                                index = 0;
                                //设置insert语句所插入字段
                                for (String key : linkedHashMap.keySet()) {
                                    index+=1;
                                    //最后一个属性
                                    if ((index+1) - count == 1){
                                        insertSql+="`"+key+"` ) values (";
                                        index = 0;
                                    }else {
                                        insertSql+="`"+key+"` ,";
                                    }
                                }
                                //设置插入的值
                                index = 0;
                                for (String key : linkedHashMap.keySet()) {
                                    index+=1;
                                    //获取value
                                    String value = (String) linkedHashMap.get(key);
                                    //判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index+1) - count == 1){
                                        insertSql+="\'"+value+"\' ),";
                                    }else {
                                        insertSql+="\'"+value+"\' ,";
                                    }
                                }
                                insertSql+=TranslationOperate.getChildrenSql(jsonObject.getString(jsonObject.getString("@pid")),resultArray,linkedHashMap,jsonObject.getString("@pid"));
                            }else {
                                return DataResult.getResult(103,"请在该JSON块中选取一个key值作为pid",null);
                            }
                        }
                    }else {
                        int count = jsonObject.keySet().size();
                        //设置insert语句所插入字段
                        for (String key : jsonObject.keySet()) {
                            index+=1;
                            //最后一个属性
                            if ((index+1) - count == 1){
                                insertSql+="`"+key+"` ) values (";
                                index = 0;
                            }else {
                                insertSql+="`"+key+"` ,";
                            }
                        }
                        //设置插入的值
                        for (String key : jsonObject.keySet()) {
                            index+=1;
                            //获取value
                            String value = (String) jsonObject.get(key);
                            //判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index+1) - count == 1){
                                insertSql+="\'"+value+"\' );";
                            }else {
                                insertSql+="\'"+value+"\' ,";
                            }
                        }
                    }
                }
            }
        }else {//是JsonArray
            JSONArray jsonArray = JSONArray.parseArray(textarea);
            JSONArray resultArray = new JSONArray();
            JSONObject tableObject = null;
            if (!jsonArray.toString().contains("@table")){
                return DataResult.getResult(103,"请指定表名称",null);
            }
            Map<String,String> replaceMap = new HashMap<>();
            replaceForms.stream().forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(),replaceForm.getNewVal());
            });
            for (Object obj : jsonArray) {
                JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                if (replaceForms.size() != 0 || !"".equals(replaceForms.get(0).getNewVal())){
                    jsonObject = TranslationOperate.replaceFields(replaceMap,jsonObject);
                }
                if (jsonObject.keySet().contains("@table")){
                    insertSql+= jsonObject.getString("@table")+" (";
                    tableObject = jsonObject;
                }else {
                    resultArray.add(jsonObject);
                }
            }
            LinkedHashMap<String,String> resultMap = new LinkedHashMap<>();
            //没有替换值
            if (replaceForms.size() == 0 || "".equals(replaceForms.get(0).getNewVal())){
                //定位索引，当in = 1 时设置添加属性
                List<Object> collect = resultArray.stream().filter(bean ->
                       bean.toString().contains("children") &&  !("[]".equals(JSONObject.parseObject(bean.toString()).getString("children")))
                ).collect(Collectors.toList());
                //不是0 说明包含父子级嵌套
                if (collect.size() != 0){
                    if (!tableObject.keySet().contains("@pid")){
                        return DataResult.getResult(103,"请指定设置pid的字段列",null);
                    }
                    //设置属性
                    JSONObject jsonObject = JSONObject.parseObject(collect.get(0).toString());
                    int count = 0;
                    if (jsonObject.keySet().contains("children")){
                        count = jsonObject.size() - 1;
                    }
                    for (String key : jsonObject.keySet()) {
                        if (!"children".equals(key)){
                            resultMap.put(key,jsonObject.getString(key));
                            index+=1;

                            //最后一个属性
                            if ((index+1) - count == 1){
                                insertSql+="`"+key+"`, `pid` ) values ";
                                index = 0;
                                resultMap.put("pid",tableObject.getString("@pid"));
                            }else {
                                insertSql+="`"+key+"` ,";
                            }
                        }
                    }
                }else {
                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                    jsonObject.remove("children");
                    //设置属性
                    int count = jsonObject.keySet().size();
                    index = 0;
                    for (String key : jsonObject.keySet()) {
                        index+=1;
                        //最后一个属性
                        if ((index+1) - count == 1){
                            insertSql+="`"+key+"` ) values ";
                            index = 0;
                        }else {
                            insertSql+="`"+key+"` ,";
                        }
                    }
                }
                int in = 0;
                for (Object obj : resultArray) {
                    in+=1;
                    JSONObject jsonObject = JSONObject.parseObject(obj.toString());

                    //如果每个JSON对象中的children数组为空或者不存在children
                    if (!jsonObject.keySet().contains("children") || "[]".equals(jsonObject.get("children").toString())){
                        //如果为空就移除children数组
                        if (jsonObject.keySet().contains("children")){
                            jsonObject.remove("children");
                        }
                        int count = jsonObject.keySet().size();
                        //设置插入的值

                        //if (in == 1){
                        //说明是不存在父子嵌套
                        if (resultMap.keySet().size() == 0){
                            index = 0;
                            for (String key : jsonObject.keySet()) {
                                index+=1;
                                //获取value
                                String value = (String) jsonObject.get(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index+1) - count == 1){
                                    insertSql+="\'"+value+"\' ),";
                                }else if (index == 1){
                                    insertSql+="( \'"+value+"\' ,";
                                }else {
                                    insertSql+="\'"+value+"\' ,";
                                }
                            }
                        }else {
                            index = 0;
                            count = resultMap.keySet().size();
                            for (String key : resultMap.keySet()) {
                                index+=1;
                                String value = jsonObject.getString(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index+1) - count == 1){
                                    insertSql+="\'"+0+"\' ),";
                                }else if (index == 1){
                                    insertSql+="( \'"+value+"\' ,";
                                }else {
                                    insertSql+="\'"+value+"\' ,";
                                }
                            }
                        }
                    }else {
                        //insert into user (`password` ,`id` ,`username`, `pid` ) values (
                        String pid = "";
                        String sql = "";
                        index = 0;
                        int count = resultMap.keySet().size();
                        for (String key : resultMap.keySet()) {
                            index+=1;
                            String value = jsonObject.getString(key);
                            //判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index+1) - count == 1){
                                insertSql+="\'"+0+"\' ),";
                                pid = jsonObject.getString(tableObject.getString("@pid"));
                            }else if (index == 1){
                                insertSql+="( \'"+value+"\' ,";
                            }else {
                                insertSql+="\'"+value+"\' ,";
                            }
                        }
                        sql+=TranslationOperate.getChildrenSql(pid,JSONArray.parseArray(jsonObject.get("children").toString()),resultMap,tableObject.getString("@pid"));
                        insertSql+=sql;
                    }
                }
            }else {//存在替换值
                if (replaceMap.keySet().contains("children") || replaceMap.keySet().contains("@pid")){
                    return DataResult.getResult(103,"默认属性不允许被替换！",null);
                }else {
                    //定位索引，当in = 1 时设置添加属性
                    List<Object> collect = resultArray.stream().filter(bean ->
                            bean.toString().contains("children") &&  !("[]".equals(JSONObject.parseObject(bean.toString()).getString("children")))
                    ).collect(Collectors.toList());
                    //不是0 说明包含父子级嵌套
                    if (collect.size() != 0){
                        if (!tableObject.keySet().contains("@pid")){
                            return DataResult.getResult(103,"请指定设置pid的字段列",null);
                        }
                        //设置属性
                        JSONObject jsonObject = JSONObject.parseObject(collect.get(0).toString());
                        int count = 0;
                        if (jsonObject.keySet().contains("children")){
                            count = jsonObject.size() - 1;
                        }
                        for (String key : jsonObject.keySet()) {
                            if (!"children".equals(key)){
                                resultMap.put(key,jsonObject.getString(key));
                                index+=1;

                                //最后一个属性
                                if ((index+1) - count == 1){
                                    insertSql+="`"+key+"`, `pid` ) values ";
                                    index = 0;
                                    resultMap.put("pid",tableObject.getString("@pid"));
                                }else {
                                    insertSql+="`"+key+"` ,";
                                }
                            }
                        }
                    }else {
                        JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                        jsonObject.remove("children");
                        //设置属性
                        int count = jsonObject.keySet().size();
                        index = 0;
                        for (String key : jsonObject.keySet()) {
                            index+=1;
                            //最后一个属性
                            if ((index+1) - count == 1){
                                insertSql+="`"+key+"` ) values ";
                                index = 0;
                            }else {
                                insertSql+="`"+key+"` ,";
                            }
                        }
                    }
                    int in = 0;
                    for (Object obj : resultArray) {
                        in+=1;
                        JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                        //jsonObject = TranslationOperate.replaceFields(replaceMap,jsonObject);
                        //如果每个JSON对象中的children数组为空或者不存在children
                        if (!jsonObject.keySet().contains("children") || "[]".equals(jsonObject.get("children").toString())){
                            //如果为空就移除children数组
                            if (jsonObject.keySet().contains("children")){
                                jsonObject.remove("children");
                            }
                            int count = jsonObject.keySet().size();
                            //设置插入的值

                            //if (in == 1){
                            //说明是不存在父子嵌套
                            if (resultMap.keySet().size() == 0){
                                index = 0;
                                for (String key : jsonObject.keySet()) {
                                    index+=1;
                                    //获取value
                                    String value = (String) jsonObject.get(key);
                                    //判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index+1) - count == 1){
                                        insertSql+="\'"+value+"\' ),";
                                    }else if (index == 1){
                                        insertSql+="( \'"+value+"\' ,";
                                    }else {
                                        insertSql+="\'"+value+"\' ,";
                                    }
                                }
                            }else {
                                index = 0;
                                count = resultMap.keySet().size();
                                for (String key : resultMap.keySet()) {
                                    index+=1;
                                    String value = jsonObject.getString(key);
                                    //判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index+1) - count == 1){
                                        insertSql+="\'"+0+"\' ),";
                                    }else if (index == 1){
                                        insertSql+="( \'"+value+"\' ,";
                                    }else {
                                        insertSql+="\'"+value+"\' ,";
                                    }
                                }
                            }
                        }else {
                            //insert into user (`password` ,`id` ,`username`, `pid` ) values (
                            String pid = "";
                            String sql = "";
                            index = 0;
                            int count = resultMap.keySet().size();
                            for (String key : resultMap.keySet()) {
                                index+=1;
                                String value = jsonObject.getString(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index+1) - count == 1){
                                    insertSql+="\'"+0+"\' ),";
                                    pid = jsonObject.getString(tableObject.getString("@pid"));
                                }else if (index == 1){
                                    insertSql+="( \'"+value+"\' ,";
                                }else {
                                    insertSql+="\'"+value+"\' ,";
                                }
                            }
                            sql+=TranslationOperate.getChildrenSql(pid,JSONArray.parseArray(jsonObject.get("children").toString()),resultMap,tableObject.getString("@pid"));
                            insertSql+=sql;

                        }
                    }
                }
            }
        }
        return DataResult.getResult(0,"",insertSql.substring(0,insertSql.length()-1)+";");
    }
}
