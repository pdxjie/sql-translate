import request from "@/utils/request"
import { METHOD_POST } from "@/constant/baseConstant"

/**
 * JSON 转换 SQL
 * @param data
 * @returns {*}
 */
export const transfromRequest = (data) => {
    return request({
        url: '/v1/transform',
        method: METHOD_POST,
        data
    })
}