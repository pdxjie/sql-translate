package com.pdx.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pdx.exception.ResponseCode;
import com.pdx.exception.Result;
import com.pdx.model.vo.ReplaceFieldVo;
import com.pdx.model.vo.TransFromVo;
import com.pdx.service.TransFromService;
import com.pdx.utils.TranslationOperate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.pdx.constant.FiledConstant.*;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: JSON 转换 SQL 接口服务实现类
 */
@Slf4j
@Service
public class TransFromServiceImpl implements TransFromService {

    /**
     * @param transFrom
     * @return
     */
    @Override
    public Result<?> transformSql(TransFromVo transFrom) throws Exception {
        Map<String, Object> result = new HashMap<>();
        if (StringUtils.isEmpty(transFrom.getType())) {
            return Result.fail(ResponseCode.PLEASE_SELECT_NEED_TRANSFORM_TYPE);
        }
        // 根据类型获取 SQL
        if ("1".equalsIgnoreCase(transFrom.getType())) {
            return getInsertSql(transFrom.getTextarea(), transFrom.getDomains());
        } else if ("2".equalsIgnoreCase(transFrom.getType())) {
            return getUpdateSql(transFrom.getTextarea(), transFrom.getDomains());
        } else if ("3".equalsIgnoreCase(transFrom.getType())) {
            return getCreateSql(transFrom.getTextarea(), transFrom.getDomains());
        }
        return Result.fail(ResponseCode.FAIL);
    }

    /**
     * 转译创建语句
     *
     * @param textarea
     * @param replaceForms
     * @return
     */
    private Result<?> getCreateSql(String textarea, List<ReplaceFieldVo> replaceForms) {
        int index = 0;
        StringBuilder createTable = new StringBuilder(CREATE_TABLE);
        JSONObject jsonObject = JSONObject.parseObject(textarea);
        if (replaceForms.isEmpty()) {
            // 判断是否存在表名
            if (!jsonObject.keySet().contains(TABLE_NAME)) {
                return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            } else {
                createTable.append("`").append(jsonObject.get(TABLE_NAME).toString()).append("`(");
                // 移除表名
                jsonObject.remove(TABLE_NAME);
                // 用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                int count = jsonObject.keySet().size();
                for (String key : jsonObject.keySet()) {
                    index += 1;
                    if (!key.contains(FILED_AID)) {
                        return Result.fail(ResponseCode.LACK_FILED_LENGTH);
                    }
                    String[] split = key.split(FILED_AID);

                    if (split.length == 1 || StringUtils.isEmpty(split[1]) || " ".equalsIgnoreCase(split[1])) {
                        return Result.fail(ResponseCode.LACK_FILED_LENGTH);
                    } else if (!split[1].matches("[0-9]+")) {
                        return Result.fail(ResponseCode.FILED_MUST_BE_INTEGER);
                    }
                    String value = "";
                    if (!CHILDREN_FILED.equalsIgnoreCase(key.split(FILED_AID)[0])) {
                        value = (String) jsonObject.get(key);
                    }
                    // 判断 key 中是否存在 id 属性，默认 id 属性为主键
                    // 如果是数字
                    if (value.matches("[0-9]+") && value.length() <= 2) {
                        //新增children start
                        if ((index + 1) - count == 1) {
                            if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                            }
                        } else {
                            if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                            }
                        }
                        // 新增 children end
                        if ((index + 1) - count == 1) {
                            if ("id".equalsIgnoreCase(split[0])) {
                                createTable.append("`").append(split[0]).append("`").append(" int(").append(split[1]).append(") primary key");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append(" int (").append(split[1]).append(")");
                            }
                        } else {
                            if ("id".equalsIgnoreCase(split[0])) {
                                createTable.append("`").append(split[0]).append("`").append(" int(").append(split[1]).append(") primary key,");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append(" int(").append(split[1]).append("),");
                            }
                        }
                    } else {
                        // 新增 children start
                        if ((index + 1) - count == 1) {
                            if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                            }
                        } else {
                            if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                            }
                        }
                        //新增children  end
                        if ((index + 1) - count == 1) {
                            if ("id".equalsIgnoreCase(split[0])) {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append(") primary key");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append(")");
                            }
                        } else {
                            if ("id".equalsIgnoreCase(split[0])) {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append(") primary key,");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append("),");
                            }
                        }
                    }
                }
                createTable.append(");");
            }
        } else {
            Map<String, String> replaceMap = new HashMap<>();
            replaceForms.stream().forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(), replaceForm.getNewVal());
            });
            // children 不能修改
            if (replaceMap.keySet().contains(CHILDREN_FILED)) {
                return Result.fail(ResponseCode.DEFAULT_ATTRIBUTE_UNABLE_TO_CHANGED);
            }
            // 判断是否存在表名
            if (!jsonObject.keySet().contains(TABLE_NAME)) {
                return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            } else {
                createTable.append("`").append(jsonObject.get(TABLE_NAME).toString()).append("`(");
                // 移除表名
                jsonObject.remove(TABLE_NAME);
                // 用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                int count = jsonObject.keySet().size();
                for (String key : jsonObject.keySet()) {
                    index += 1;
                    // 判断是否设置字段长度
                    if (!key.contains(FILED_AID)) {
                        return Result.fail(ResponseCode.LACK_FILED_LENGTH);
                    }
                    String[] split = key.split(FILED_AID);
                    if (split.length == 1 || StringUtils.isEmpty(split[1]) || " ".equalsIgnoreCase(split[1])) {
                        return Result.fail(ResponseCode.LACK_FILED_LENGTH);
                    } else if (!split[1].matches("[0-9]+")) {
                        return Result.fail(ResponseCode.FILED_MUST_BE_INTEGER);
                    }
                    String value = "";
                    if (!CHILDREN_FILED.equalsIgnoreCase(key.split(FILED_AID)[0])) {
                        value = (String) jsonObject.get(key);
                    }
                    if (replaceMap.containsKey(split[0])) {
                        String newKey = replaceMap.get(split[0]);
                        if (value.matches("[0-9]+") && value.length() <= 2) {
                            // 新增 children start
                            if ((index + 1) - count == 1) {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                                }
                            } else {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                                }
                            }
                            // 新增 children end
                            if ((index + 1) - count == 1) {
                                createTable.append("`").append(newKey).append("`").append(" int(").append(split[1]).append(")");
                            } else {
                                createTable.append("`").append(newKey).append("`").append(" int(").append(split[1]).append("),");
                            }
                        } else {

                            // 新增 children start
                            if ((index + 1) - count == 1) {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                                }
                            } else {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                                }
                            }
                            // 新增 children end
                            if ((index + 1) - count == 1) {
                                createTable.append("`").append(newKey).append("`").append(" varchar(").append(split[1]).append(")");
                            } else {
                                createTable.append("`").append(newKey).append("`").append(" varchar(").append(split[1]).append("),");
                            }
                        }
                    } else {
                        if (value.matches("[0-9]+") && value.length() <= 2) {
                            // 新增 children start
                            if ((index + 1) - count == 1) {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                                }
                            } else {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                                }
                            }
                            //新增children  end
                            if ((index + 1) - count == 1) {
                                createTable.append("`").append(split[0]).append("`").append(" int(").append(split[1]).append(")");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append("int(").append(split[1]).append("),");
                            }
                        } else {
                            // 新增 children start
                            if ((index + 1) - count == 1) {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append(") ");
                                }
                            } else {
                                if (CHILDREN_FILED.equalsIgnoreCase(split[0])) {
                                    createTable.append(PID_FILED + " varchar(").append(split[1]).append("), ");
                                }
                            }
                            //新增children  end
                            if ((index + 1) - count == 1) {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append(")");
                            } else {
                                createTable.append("`").append(split[0]).append("`").append(" varchar(").append(split[1]).append("),");
                            }
                        }
                    }
                }
                createTable.append(");");
            }
        }
        return Result.success(createTable.toString());
    }

    /**
     * 转译更新语句
     *
     * @param textarea
     * @param replaceForms
     * @return
     */
    private Result<?> getUpdateSql(String textarea, List<ReplaceFieldVo> replaceForms) {
        Integer index = 0;
        StringBuilder updateSql = new StringBuilder("update ");
        // 保存where条件的属性和值
        Map<String, String> conditionMap = new HashMap<>();
        JSONObject jsonObject = JSONObject.parseObject(textarea);
        // 判断是否存在替换属性
        if (replaceForms.isEmpty()) {
            if (!jsonObject.keySet().contains(TABLE_NAME)) {
                 return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            } else {
                updateSql.append("`").append(jsonObject.get(TABLE_NAME).toString()).append("` ");
                // 移除表名
                jsonObject.remove(TABLE_NAME);
                // 判断是否存在更新条件
                List<String> condition = jsonObject.keySet().stream().filter(key ->
                        key.contains("#")
                ).collect(Collectors.toList());
                // 保存条件
                for (String key : condition) {
                    conditionMap.put(key, jsonObject.get(key).toString());
                }
                if (condition.isEmpty()) {
                    return Result.fail(ResponseCode.PLEASE_MAKE_SURE_THE_CONDITION_AFTER_WHERE);
                } else {
                    for (String con : condition) {
                        jsonObject.remove(con);
                    }
                    //用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                    int count = jsonObject.keySet().size();
                    for (String key : jsonObject.keySet()) {
                        index += 1;
                        if ((index + 1) - count == 1) {
                            updateSql.append(" `").append(key).append("` =").append("\'").append(jsonObject.get(key).toString()).append("\' where ");
                        } else {
                            if (index == 1) {
                                updateSql.append("set `").append(key).append("` =").append("\'").append(jsonObject.get(key).toString()).append("\',");
                            } else {
                                updateSql.append(" `").append(key).append("` =").append("\'").append(jsonObject.get(key).toString()).append("\',");
                            }

                        }
                    }
                }
            }
        } else {
            // 替换属性
            Map<String, String> replaceMap = new HashMap<>();
            replaceForms.forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(), replaceForm.getNewVal());
            });
            // 是否存在表名
            if (!jsonObject.containsKey(TABLE_NAME)) {
                 return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            } else {
                updateSql.append("`").append(jsonObject.get(TABLE_NAME).toString()).append("` ");
                // 移除表名
                jsonObject.remove(TABLE_NAME);
                // 替换属性
                JSONObject result = new JSONObject();
                for (String key : jsonObject.keySet()) {
                    for (String Jkey : replaceMap.keySet()) {
                        // 判断修改字段是否为条件字段
                        if (key.contains("#")) {
                            String[] split = key.split("#");
                            if (split[0].equalsIgnoreCase(Jkey)) {
                                result.put(split[0].replace(Jkey, replaceMap.get(Jkey)) + "#" + split[1], jsonObject.get(key));
                                break;
                            } else {
                                result.put(key, jsonObject.get(key));
                                break;
                            }
                        } else {
                            if (key.equalsIgnoreCase(Jkey)) {
                                result.put(key.replace(Jkey, replaceMap.get(Jkey)), jsonObject.get(key));
                                break;
                            } else {
                                if (!replaceMap.containsKey(key)) {
                                    result.put(key, jsonObject.get(key));
                                }
                            }
                        }
                    }
                }
                // 判断是否存在更新条件
                List<String> condition = result.keySet().stream().filter(key ->
                        key.contains("#")
                ).collect(Collectors.toList());
                // 保存条件
                for (String key : condition) {
                    conditionMap.put(key, result.getString(key));
                }
                if (condition.isEmpty()) {
                    return Result.fail(ResponseCode.PLEASE_MAKE_SURE_THE_CONDITION_AFTER_WHERE);
                } else {
                    for (String con : condition) {
                        result.remove(con);
                    }
                    // 用来判断是否是最后一个字段，最后一个字段不加逗号结尾
                    int count = result.keySet().size();
                    for (String key : result.keySet()) {
                        index += 1;
                        if (replaceMap.containsKey(key)) {
                            String newKey = replaceMap.get(key);
                            if ((index + 1) - count == 1) {
                                updateSql.append(" ").append(newKey).append("=").append(result.get(key).toString()).append(" where ");
                            } else {
                                if (index == 1) {
                                    updateSql.append("set `").append(newKey).append("` =").append("\'").append(result.get(key).toString()).append("\',");
                                } else {
                                    updateSql.append(" `").append(newKey).append("` =").append("\'").append(result.get(key).toString()).append("\',");
                                }
                            }
                        } else {
                            if ((index + 1) - count == 1) {
                                updateSql.append(" `").append(key).append("` =").append("\'").append(result.get(key).toString()).append("\' where ");
                            } else {
                                if (index == 1) {
                                    updateSql.append("set `").append(key).append("` =").append("\'").append(result.get(key).toString()).append("\',");
                                } else {
                                    updateSql.append(" `").append(key).append("` =").append("\'").append(result.get(key).toString()).append("\',");
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
            index += 1;
            String[] condition = key.split("#");
            if ((index + 1) - size == 1) {
                updateSql.append("`").append(condition[0]).append("` ").append(condition[1]).append(" ").append("\'").append(value).append("\';");
            } else {
                updateSql.append("`").append(condition[0]).append("` ").append(condition[1]).append(" ").append("\'").append(value).append("\' and ");
            }

        }
        return Result.success(updateSql.toString());
    }

    /**
     * 转译插入语句 需要指定pid
     *
     * @param textarea
     * @param replaceForms
     * @return
     */
    private Result<?> getInsertSql(String textarea, List<ReplaceFieldVo> replaceForms) throws Exception {
        StringBuilder insertSql = new StringBuilder("insert into ");
        int index = 0;
        // 先判断是 JsonArray 还是 JsonObject
        if (!textarea.startsWith("[")) { // 说明是 JsonObject
            JSONObject jsonObject = JSONObject.parseObject(textarea);
            // 判断是否存在表
            if (jsonObject.containsKey(TABLE_NAME)) {
                insertSql.append("`").append(jsonObject.get(TABLE_NAME)).append("` (");
            } else {
                 return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            }
            // 移除 @table
            jsonObject.remove(TABLE_NAME);
            // 没有需要替换的属性
            // || 后面的含义是有空值传入
            if (replaceForms.isEmpty()) {
                // 判断是否存在多级嵌套
                if (jsonObject.containsKey(CHILDREN_FILED)) {
                    // 如果 children 数组中没有值，添加了 @pid 直接去掉
                    if ("[]".equalsIgnoreCase(jsonObject.get(CHILDREN_FILED).toString())) { //存在 children，但 children 数组中不存在值
                        jsonObject.remove(CHILDREN_FILED);
                        if (jsonObject.containsKey("@pid")) {
                            // 如果存在则移除
                            jsonObject.remove("@pid");
                        }
                        int count = jsonObject.keySet().size();
                        //设置 insert 语句所插入字段
                        for (String key : jsonObject.keySet()) {
                            index += 1;
                            // 最后一个属性
                            if ((index + 1) - count == 1) {
                                insertSql.append("`").append(key).append("` ) values (");
                                index = 0;
                            } else {
                                insertSql.append("`").append(key).append("` ,");
                            }
                        }
                        //设置插入的值
                        for (String key : jsonObject.keySet()) {
                            index += 1;
                            //获取value
                            String value = (String) jsonObject.get(key);
                            //判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index + 1) - count == 1) {
                                insertSql.append("\'").append(value).append("\' );");
                            } else {
                                insertSql.append("\'").append(value).append("\' ,");
                            }
                        }
                    } else {
                        if (jsonObject.containsKey("@pid")) {
                            JSONArray resultArray = new JSONArray();
                            String children = jsonObject.get(CHILDREN_FILED).toString();
                            resultArray = JSONArray.parseArray(children);
                            jsonObject.remove(CHILDREN_FILED);
                            LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                            // 将 key 存入 linkedHashMap 中保证插入字段的顺序
                            for (String key : jsonObject.keySet()) {
                                if (key.equalsIgnoreCase("@pid")) {
                                    linkedHashMap.put(key.replace(FILED_AID, ""), "0");
                                } else {
                                    linkedHashMap.put(key, jsonObject.getString(key));
                                }
                            }
                            int count = linkedHashMap.size();
                            index = 0;
                            //设置insert语句所插入字段
                            for (String key : linkedHashMap.keySet()) {
                                index += 1;
                                //最后一个属性
                                if ((index + 1) - count == 1) {
                                    insertSql.append("`").append(key).append("` ) values (");
                                    index = 0;
                                } else {
                                    insertSql.append("`").append(key).append("` ,");
                                }
                            }
                            //设置插入的值
                            index = 0;
                            for (String key : linkedHashMap.keySet()) {
                                index += 1;
                                //获取value
                                String value = (String) linkedHashMap.get(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index + 1) - count == 1) {
                                    insertSql.append("\'").append(value).append("\' ),");
                                } else {
                                    insertSql.append("\'").append(value).append("\' ,");
                                }
                            }
                            insertSql.append(TranslationOperate.getChildrenSql(jsonObject.getString(jsonObject.getString("@pid")), resultArray, linkedHashMap, jsonObject.getString("@pid")));
                        } else {
                            return Result.fail(ResponseCode.PLEASE_SELECTED_PID);
                        }
                    }
                } else {
                    int count = jsonObject.keySet().size();
                    // 设置 insert 语句所插入字段
                    for (String key : jsonObject.keySet()) {
                        index += 1;
                        // 最后一个属性
                        if ((index + 1) - count == 1) {
                            insertSql.append("`").append(key).append("` ) values (");
                            index = 0;
                        } else {
                            insertSql.append("`").append(key).append("` ,");
                        }
                    }
                    // 设置插入的值
                    for (String key : jsonObject.keySet()) {
                        index += 1;
                        // 获取value
                        String value = (String) jsonObject.get(key);
                        // 判断是否是最后一个属性，最后一个属性不加逗号
                        if ((index + 1) - count == 1) {
                            insertSql.append("\'").append(value).append("\' );");
                        } else {
                            insertSql.append("\'").append(value).append("\' ,");
                        }
                    }
                }
            } else { // 有替换值
                Map<String, String> replaceMap = new HashMap<>();
                replaceForms.forEach(replaceForm -> {
                    replaceMap.put(replaceForm.getOldVal(), replaceForm.getNewVal());
                });
                if (replaceMap.containsKey(CHILDREN_FILED) || replaceMap.containsKey("@pid")) {
                    return Result.fail(ResponseCode.DEFAULT_ATTRIBUTE_UNABLE_TO_CHANGED);
                } else {
                    String pidKey = "";
                    if (jsonObject.containsKey("@pid")) {
                        pidKey = jsonObject.getString("@pid");
                    }
                    // 递归替换属性
                    jsonObject = TranslationOperate.replaceFields(replaceMap, jsonObject);
                    if (replaceMap.containsKey(pidKey)) {
                        jsonObject.put("@pid", replaceMap.get(pidKey));
                    } else {
                        if (!replaceMap.keySet().isEmpty()) {
                            jsonObject.put("@pid", pidKey);
                        }
                    }
                    // 判断是否存在多级嵌套
                    if (jsonObject.containsKey(CHILDREN_FILED)) {
                        // 如果 children 数组中没有值，添加了 @pid 直接去掉
                        if ("[]".equalsIgnoreCase(jsonObject.get(CHILDREN_FILED).toString())) {//存在children，但children数组中不存在值
                            jsonObject.remove(CHILDREN_FILED);
                            if (jsonObject.containsKey("@pid")) {
                                // 如果存在则移除
                                jsonObject.remove("@pid");
                            }
                            int count = jsonObject.keySet().size();
                            // 设置 insert 语句所插入字段
                            for (String key : jsonObject.keySet()) {
                                index += 1;
                                // 最后一个属性
                                if ((index + 1) - count == 1) {
                                    insertSql.append("`").append(key).append("` ) values (");
                                    index = 0;
                                } else {
                                    insertSql.append("`").append(key).append("` ,");
                                }
                            }
                            // 设置插入的值
                            for (String key : jsonObject.keySet()) {
                                index += 1;
                                // 获取 value
                                String value = (String) jsonObject.get(key);
                                // 判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index + 1) - count == 1) {
                                    insertSql.append("\'").append(value).append("\' );");
                                } else {
                                    insertSql.append("\'").append(value).append("\' ,");
                                }
                            }
                        } else {
                            if (jsonObject.containsKey("@pid")) {
                                JSONArray resultArray = new JSONArray();
                                String children = jsonObject.get(CHILDREN_FILED).toString();
                                resultArray = JSONArray.parseArray(children);
                                jsonObject.remove(CHILDREN_FILED);
                                LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
                                // 将 key 存入 linkedHashMap 中保证插入字段的顺序
                                for (String key : jsonObject.keySet()) {
                                    if (key.equalsIgnoreCase("@pid")) {
                                        linkedHashMap.put(key.replace(FILED_AID, ""), "0");
                                    } else {
                                        linkedHashMap.put(key, jsonObject.getString(key));
                                    }
                                }
                                int count = linkedHashMap.size();
                                index = 0;
                                // 设置 insert 语句所插入字段
                                for (String key : linkedHashMap.keySet()) {
                                    index += 1;
                                    // 最后一个属性
                                    if ((index + 1) - count == 1) {
                                        insertSql.append("`").append(key).append("` ) values (");
                                        index = 0;
                                    } else {
                                        insertSql.append("`").append(key).append("` ,");
                                    }
                                }
                                // 设置插入的值
                                index = 0;
                                for (String key : linkedHashMap.keySet()) {
                                    index += 1;
                                    // 获取 value
                                    String value = (String) linkedHashMap.get(key);
                                    // 判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index + 1) - count == 1) {
                                        insertSql.append("\'").append(value).append("\' ),");
                                    } else {
                                        insertSql.append("\'").append(value).append("\' ,");
                                    }
                                }
                                insertSql.append(TranslationOperate.getChildrenSql(jsonObject.getString(jsonObject.getString("@pid")), resultArray, linkedHashMap, jsonObject.getString("@pid")));
                            } else {
                                return Result.fail(ResponseCode.PLEASE_SELECTED_PID);
                            }
                        }
                    } else {
                        int count = jsonObject.keySet().size();
                        // 设置 insert 语句所插入字段
                        for (String key : jsonObject.keySet()) {
                            index += 1;
                            // 最后一个属性
                            if ((index + 1) - count == 1) {
                                insertSql.append("`").append(key).append("` ) values (");
                                index = 0;
                            } else {
                                insertSql.append("`").append(key).append("` ,");
                            }
                        }
                        // 设置插入的值
                        for (String key : jsonObject.keySet()) {
                            index += 1;
                            // 获取 value
                            String value = (String) jsonObject.get(key);
                            // 判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index + 1) - count == 1) {
                                insertSql.append("\'").append(value).append("\' );");
                            } else {
                                insertSql.append("\'").append(value).append("\' ,");
                            }
                        }
                    }
                }
            }
        } else { // 是 JsonArray
            JSONArray jsonArray = JSONArray.parseArray(textarea);
            JSONArray resultArray = new JSONArray();
            JSONObject tableObject = null;
            if (!jsonArray.toString().contains(TABLE_NAME)) {
                return Result.fail(ResponseCode.PLEASE_SET_UP_TABLE_NAME);
            }
            Map<String, String> replaceMap = new HashMap<>();
            replaceForms.forEach(replaceForm -> {
                replaceMap.put(replaceForm.getOldVal(), replaceForm.getNewVal());
            });
            for (Object obj : jsonArray) {
                JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                if (!replaceForms.isEmpty()) {
                    jsonObject = TranslationOperate.replaceFields(replaceMap, jsonObject);
                }
                if (jsonObject.containsKey(TABLE_NAME)) {
                    insertSql.append(jsonObject.getString(TABLE_NAME)).append(" (");
                    tableObject = jsonObject;
                } else {
                    resultArray.add(jsonObject);
                }
            }
            LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
            // 没有替换值
            if (replaceForms.isEmpty()) {
                // 定位索引，当 in = 1 时设置添加属性
                List<Object> collect = resultArray.stream().filter(bean ->
                        bean.toString().contains(CHILDREN_FILED) && !("[]".equalsIgnoreCase(JSONObject.parseObject(bean.toString()).getString(CHILDREN_FILED)))
                ).collect(Collectors.toList());
                // 不是 0 说明包含父子级嵌套
                if (!collect.isEmpty()) {
                    if (!tableObject.containsKey("@pid")) {
                        return Result.fail(ResponseCode.PLEASE_SELECTED_PID);
                    }
                    // 设置属性
                    JSONObject jsonObject = JSONObject.parseObject(collect.get(0).toString());
                    int count = 0;
                    if (jsonObject.containsKey(CHILDREN_FILED)) {
                        count = jsonObject.size() - 1;
                    }
                    for (String key : jsonObject.keySet()) {
                        if (!CHILDREN_FILED.equalsIgnoreCase(key)) {
                            resultMap.put(key, jsonObject.getString(key));
                            index += 1;

                            // 最后一个属性
                            if ((index + 1) - count == 1) {
                                insertSql.append("`").append(key).append("`, `pid` ) values ");
                                index = 0;
                                resultMap.put("pid", tableObject.getString("@pid"));
                            } else {
                                insertSql.append("`").append(key).append("` ,");
                            }
                        }
                    }
                } else {
                    JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                    jsonObject.remove(CHILDREN_FILED);
                    // 设置属性
                    int count = jsonObject.keySet().size();
                    index = 0;
                    for (String key : jsonObject.keySet()) {
                        index += 1;
                        // 最后一个属性
                        if ((index + 1) - count == 1) {
                            insertSql.append("`").append(key).append("` ) values ");
                            index = 0;
                        } else {
                            insertSql.append("`").append(key).append("` ,");
                        }
                    }
                }
                int in = 0;
                for (Object obj : resultArray) {
                    in += 1;
                    JSONObject jsonObject = JSONObject.parseObject(obj.toString());

                    // 如果每个 JSON 对象中的 children 数组为空或者不存在 children
                    if (!jsonObject.containsKey(CHILDREN_FILED) || "[]".equalsIgnoreCase(jsonObject.get(CHILDREN_FILED).toString())) {
                        // 如果为空就移除 children 数组
                        if (jsonObject.containsKey(CHILDREN_FILED)) {
                            jsonObject.remove(CHILDREN_FILED);
                        }
                        int count = jsonObject.keySet().size();
                        // 设置插入的值
                        // 说明是不存在父子嵌套
                        if (resultMap.keySet().isEmpty()) {
                            index = 0;
                            for (String key : jsonObject.keySet()) {
                                index += 1;
                                //获取value
                                String value = (String) jsonObject.get(key);
                                //判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index + 1) - count == 1) {
                                    insertSql.append("\'").append(value).append("\' ),");
                                } else if (index == 1) {
                                    insertSql.append("( \'").append(value).append("\' ,");
                                } else {
                                    insertSql.append("\'").append(value).append("\' ,");
                                }
                            }
                        } else {
                            index = 0;
                            count = resultMap.keySet().size();
                            for (String key : resultMap.keySet()) {
                                index += 1;
                                String value = jsonObject.getString(key);
                                // 判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index + 1) - count == 1) {
                                    insertSql.append("\'" + 0 + "\' ),");
                                } else if (index == 1) {
                                    insertSql.append("( \'").append(value).append("\' ,");
                                } else {
                                    insertSql.append("\'").append(value).append("\' ,");
                                }
                            }
                        }
                    } else {
                        String pid = "";
                        String sql = "";
                        index = 0;
                        int count = resultMap.keySet().size();
                        for (String key : resultMap.keySet()) {
                            index += 1;
                            String value = jsonObject.getString(key);
                            //判断是否是最后一个属性，最后一个属性不加逗号
                            if ((index + 1) - count == 1) {
                                insertSql.append("\'" + 0 + "\' ),");
                                pid = jsonObject.getString(tableObject.getString("@pid"));
                            } else if (index == 1) {
                                insertSql.append("( \'").append(value).append("\' ,");
                            } else {
                                insertSql.append("\'").append(value).append("\' ,");
                            }
                        }
                        sql += TranslationOperate.getChildrenSql(pid, JSONArray.parseArray(jsonObject.get(CHILDREN_FILED).toString()), resultMap, tableObject.getString("@pid"));
                        insertSql.append(sql);
                    }
                }
            } else { // 存在替换值
                if (replaceMap.containsKey(CHILDREN_FILED) || replaceMap.containsKey("@pid")) {
                    return Result.fail(ResponseCode.DEFAULT_ATTRIBUTE_UNABLE_TO_CHANGED);
                } else {
                    // 定位索引，当 in = 1 时设置添加属性
                    List<Object> collect = resultArray.stream().filter(bean ->
                            bean.toString().contains(CHILDREN_FILED) && !("[]".equalsIgnoreCase(JSONObject.parseObject(bean.toString()).getString(CHILDREN_FILED)))
                    ).collect(Collectors.toList());
                    // 不是 0 说明包含父子级嵌套
                    if (!collect.isEmpty()) {
                        if (!tableObject.containsKey("@pid")) {
                            return Result.fail(ResponseCode.PLEASE_SELECTED_PID);
                        }
                        // 设置属性
                        JSONObject jsonObject = JSONObject.parseObject(collect.get(0).toString());
                        int count = 0;
                        if (jsonObject.containsKey(CHILDREN_FILED)) {
                            count = jsonObject.size() - 1;
                        }
                        for (String key : jsonObject.keySet()) {
                            if (!CHILDREN_FILED.equalsIgnoreCase(key)) {
                                resultMap.put(key, jsonObject.getString(key));
                                index += 1;
                                // 最后一个属性
                                if ((index + 1) - count == 1) {
                                    insertSql.append("`").append(key).append("`, `pid` ) values ");
                                    index = 0;
                                    resultMap.put("pid", tableObject.getString("@pid"));
                                } else {
                                    insertSql.append("`").append(key).append("` ,");
                                }
                            }
                        }
                    } else {
                        JSONObject jsonObject = JSONObject.parseObject(jsonArray.get(0).toString());
                        jsonObject.remove(CHILDREN_FILED);
                        //设置属性
                        int count = jsonObject.keySet().size();
                        index = 0;
                        for (String key : jsonObject.keySet()) {
                            index += 1;
                            // 最后一个属性
                            if ((index + 1) - count == 1) {
                                insertSql.append("`").append(key).append("` ) values ");
                                index = 0;
                            } else {
                                insertSql.append("`").append(key).append("` ,");
                            }
                        }
                    }
                    int in = 0;
                    for (Object obj : resultArray) {
                        in += 1;
                        JSONObject jsonObject = JSONObject.parseObject(obj.toString());
                        // 如果每个 JSON 对象中的 children 数组为空或者不存在 children
                        if (!jsonObject.containsKey(CHILDREN_FILED) || "[]".equalsIgnoreCase(jsonObject.get(CHILDREN_FILED).toString())) {
                            // 如果为空就移除 children 数组
                            if (jsonObject.containsKey(CHILDREN_FILED)) {
                                jsonObject.remove(CHILDREN_FILED);
                            }
                            int count = jsonObject.keySet().size();
                            // 设置插入的值
                            // 说明是不存在父子嵌套
                            if (resultMap.keySet().isEmpty()) {
                                index = 0;
                                for (String key : jsonObject.keySet()) {
                                    index += 1;
                                    // 获取 value
                                    String value = (String) jsonObject.get(key);
                                    // 判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index + 1) - count == 1) {
                                        insertSql.append("\'").append(value).append("\' ),");
                                    } else if (index == 1) {
                                        insertSql.append("( \'").append(value).append("\' ,");
                                    } else {
                                        insertSql.append("\'").append(value).append("\' ,");
                                    }
                                }
                            } else {
                                index = 0;
                                count = resultMap.keySet().size();
                                for (String key : resultMap.keySet()) {
                                    index += 1;
                                    String value = jsonObject.getString(key);
                                    // 判断是否是最后一个属性，最后一个属性不加逗号
                                    if ((index + 1) - count == 1) {
                                        insertSql.append("\'" + 0 + "\' ),");
                                    } else if (index == 1) {
                                        insertSql.append("( \'").append(value).append("\' ,");
                                    } else {
                                        insertSql.append("\'").append(value).append("\' ,");
                                    }
                                }
                            }
                        } else {
                            String pid = "";
                            String sql = "";
                            index = 0;
                            int count = resultMap.keySet().size();
                            for (String key : resultMap.keySet()) {
                                index += 1;
                                String value = jsonObject.getString(key);
                                // 判断是否是最后一个属性，最后一个属性不加逗号
                                if ((index + 1) - count == 1) {
                                    insertSql.append("\'" + 0 + "\' ),");
                                    pid = jsonObject.getString(tableObject.getString("@pid"));
                                } else if (index == 1) {
                                    insertSql.append("( \'").append(value).append("\' ,");
                                } else {
                                    insertSql.append("\'").append(value).append("\' ,");
                                }
                            }
                            sql += TranslationOperate.getChildrenSql(pid, JSONArray.parseArray(jsonObject.get(CHILDREN_FILED).toString()), resultMap, tableObject.getString("@pid"));
                            insertSql.append(sql);

                        }
                    }
                }
            }
        }
        return Result.success(insertSql.substring(0, insertSql.length() - 1) + ";");
    }
}
