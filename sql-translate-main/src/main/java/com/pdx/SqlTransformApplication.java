package com.pdx;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: IT 派同学
 * @DateTime: 2023/11/20
 * @Description: 主启动类
 */
@Slf4j
@SpringBootApplication
public class SqlTransformApplication {

    public static void main(String[] args) {
        SpringApplication.run(SqlTransformApplication.class, args);
    }
}
