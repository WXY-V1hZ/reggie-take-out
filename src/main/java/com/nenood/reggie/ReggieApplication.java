package com.nenood.reggie;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication // 默认扫描注解所在包及其所有子包
@Slf4j
@ServletComponentScan("com.nenood.reggie.filter")
@EnableTransactionManagement
@EnableCaching
public class ReggieApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class, args);
        log.info("启动成功，项目运行中...");
    }

}
