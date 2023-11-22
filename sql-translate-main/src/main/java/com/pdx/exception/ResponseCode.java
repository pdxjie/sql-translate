package com.pdx.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/*
 * @Author IT派同学
 * @Description 响应枚举类
 * @Date 2023/7/24
 **/
@AllArgsConstructor
@Getter
public enum ResponseCode {

    SUCCESS(200, "操作成功"),

    FAIL(201, "操作失败"),

    PLEASE_SUPPLEMENT_NEED_TRANSFORM_DATA(202, "请补充需要转换的数据"),

    PLEASE_SELECT_NEED_TRANSFORM_TYPE(203, "请选择需要转译类型"),

    PLEASE_SET_UP_TABLE_NAME(204, "请设置表名称"),

    LACK_FILED_LENGTH(205, "缺少字段长度"),

    FILED_MUST_BE_INTEGER(206, "字段长度必须为整数类型"),

    DEFAULT_ATTRIBUTE_UNABLE_TO_CHANGED(207, "默认属性无法修改"),

    PLEASE_MAKE_SURE_THE_CONDITION_AFTER_WHERE(208, "请指定 WHERE 子句后面的更新条件"),

    PLEASE_SELECTED_PID(209, "请在该 JSON 块中选取一个 KEY 值作为 PID"),

    ;

    // 状态码
    private Integer code;

    // 响应信息
    private String message;

}
