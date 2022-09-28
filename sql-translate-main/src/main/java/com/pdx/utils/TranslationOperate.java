package com.pdx.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 派 大 星
 * @function
 * @date 2022/9/26 22:54
 * @website https://blog.csdn.net/Gaowumao
 */
@Slf4j
public class TranslationOperate {


    /**
     * JsonObject递归替换属性
     * 当@pid所指向的字段被替换，那么@pid的value也将随之被替换
     * @param replaceMap
     * @param jsonObject
     * @return
     */
    public static JSONObject replaceFields(Map<String, String> replaceMap, JSONObject jsonObject) {
        //如果不包含子集 也就是 是否包含children
        if (!jsonObject.keySet().contains("children")){
            //替换属性
            JSONObject result = new JSONObject();
            for (String key : jsonObject.keySet()) {
                for (String Jkey : replaceMap.keySet()) {
                    if (key.equals(Jkey)){
                        result.put(key.replace(Jkey, replaceMap.get(Jkey)),jsonObject.get(key));
                        break;
                    }else {
                        result.put(key,jsonObject.get(key));
                        break;
                    }
                }
            }
            jsonObject = result;
        }else {
            JSONObject result = new JSONObject();
            if ("[]".equals(jsonObject.getString("children"))) {
                jsonObject.remove("children");
                for (String key : jsonObject.keySet()) {
                    for (String Jkey : replaceMap.keySet()) {
                        //判断替换属性时是否指定pid值，如果指定pid中的属性，需要同时替换
                        if ("@pid".equals(key)){
                            if (Jkey.equals(jsonObject.getString(key))){
                                result.put("@pid",replaceMap.get(Jkey));
                            }
                        }
                        if (key.equals(Jkey)){
                            result.put(key.replace(Jkey, replaceMap.get(Jkey)),jsonObject.get(key));
                            break;
                        }else {
                            if (!"@pid".equals(key)){
                                if (replaceMap.keySet().contains(key)){
                                    continue;
                                }else {
                                    result.put(key,jsonObject.get(key));
                                    break;
                                }
                            }
                        }
                    }
                }
                jsonObject = result;
            }else {
                JSONObject child = new JSONObject();
                JSONArray jsonArray = JSONArray.parseArray(jsonObject.get("children").toString());
                jsonObject.remove("children");
                for (String key : jsonObject.keySet()) {
                    for (String Jkey : replaceMap.keySet()) {
                        //判断替换属性时是否指定pid值，如果指定pid中的属性，需要同时替换
                        if ("@pid".equals(key)){
                            if (Jkey.equals(jsonObject.getString(key))){
                                result.put("@pid",replaceMap.get(Jkey));
                            }
                        }
                        if (key.equals(Jkey)){
                            result.put(key.replace(Jkey, replaceMap.get(Jkey)),jsonObject.get(key));
                            break;
                        }else {
                            if (!"@pid".equals(key)){
                                if (replaceMap.keySet().contains(key)){
                                    continue;
                                }else {
                                    result.put(key,jsonObject.get(key));
                                    break;
                                }
                            }
                        }
                    }
                }
                List<JSONObject> list = new ArrayList<>();
                for (Object array : jsonArray) {
                    JSONObject children = JSONObject.parseObject(array.toString());
                    JSONObject replaceFields = replaceFields(replaceMap, children);

                    list.add(replaceFields);
                    result.put("children",list);
                }
                child = result;
                jsonObject = child;
            }
        }
        return jsonObject;
    }


    /**
     * 获取子语句
     * @param pid pid值
     * @param resultArray
     * @param linkedHashMap
     * @param pidKey pid对应的key
     * @return
     */
    public static String getChildrenSql(String pid, JSONArray resultArray, LinkedHashMap<String, String> linkedHashMap, String pidKey) {
        int index = 0;
        String resultSql = "";
        if (resultArray.toString().contains("children")){
            int length = 0;
            int resultLength = resultArray.size();
            for (Object result : resultArray) {
                JSONObject jsonObject = JSONObject.parseObject(result.toString());
                if ("[]".equals(jsonObject.get("children").toString())){
                    length+=1;
                    String valueSql = "( ";
                    int count = linkedHashMap.keySet().size();
                    index = 0;
                    for (String pKey : linkedHashMap.keySet()) {
                        index += 1;
                        for (String key : jsonObject.keySet()) {
                            if (pKey.equals(key)){//按顺序拼接
                                if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                    valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                    break;
                                }else if ((index + 1) - count == 1){
                                    valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                    break;
                                }else {
                                    valueSql+="\'"+jsonObject.getString(key)+"\',";
                                    break;
                                }
                            }else if ("pid".equals(pKey)){
                                if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                    valueSql+="\'"+pid+"\'),";
                                    break;
                                }else if ((index + 1) - count == 1){
                                    valueSql+="\'"+pid+"\'),";
                                    break;
                                }else {
                                    valueSql+="\'"+pid+"\',";
                                    break;
                                }
                            }
                        }
                    }
                    resultSql+=valueSql;
                }else {
                    JSONArray jsonArray = new JSONArray();
                    jsonArray = (JSONArray) jsonObject.get("children");
                    jsonObject.remove("children");
                    length+=1;
                    String valueSql = "( ";
                    int count = linkedHashMap.keySet().size();
                    index = 0;
                    for (String pKey : linkedHashMap.keySet()) {
                        index += 1;
                        for (String key : jsonObject.keySet()) {
                            if (pKey.equals(key)){//按顺序拼接
                                if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                    valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                    break;
                                }else if ((index + 1) - count == 1){
                                    valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                    break;
                                }else {
                                    valueSql+="\'"+jsonObject.getString(key)+"\',";
                                    break;
                                }
                            }else if ("pid".equals(pKey)){
                                if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                    valueSql+="\'"+pid+"\'),";
                                    break;
                                }else if ((index + 1) - count == 1){
                                    valueSql+="\'"+pid+"\'),";
                                    break;
                                }else {
                                    valueSql+="\'"+pid+"\',";
                                    break;
                                }
                            }
                        }
                    }
                    resultSql+=valueSql;
                    resultSql+=getChildrenSql(jsonObject.getString(pidKey),jsonArray,linkedHashMap,pidKey);
                }
            }
        }else {
            int length = 0;
            int resultLength = resultArray.size();
            for (Object result : resultArray) {
                length+=1;
                String valueSql = "( ";
                JSONObject jsonObject = JSONObject.parseObject(result.toString());
                //判断子json中是否包含children字段 如果存在则判断是否存在值
                int count = linkedHashMap.keySet().size();
                index = 0;
                for (String pKey : linkedHashMap.keySet()) {
                    index += 1;
                    for (String key : jsonObject.keySet()) {
                        if (pKey.equals(key)){//按顺序拼接
                            if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                break;
                            }else if ((index + 1) - count == 1){
                                valueSql+="\'"+jsonObject.getString(key)+"\'),";
                                break;
                            }else {
                                valueSql+="\'"+jsonObject.getString(key)+"\',";
                                break;
                            }
                        }else if ("pid".equals(pKey)){
                            if ((index + 1) - count == 1 && (length+1) - resultLength == 1){
                                valueSql+="\'"+pid+"\'),";
                                break;
                            }else if ((index + 1) - count == 1){
                                valueSql+="\'"+pid+"\'),";
                                break;
                            }else {
                                valueSql+="\'"+pid+"\',";
                                break;
                            }
                        }
                    }
                }
                resultSql+=valueSql;
            }
        }
        return resultSql;
    }
}
