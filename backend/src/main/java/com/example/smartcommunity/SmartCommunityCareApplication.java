package com.example.smartcommunity;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication(exclude = {RedisAutoConfiguration.class, RedisRepositoriesAutoConfiguration.class})
@MapperScan("com.example.smartcommunity.mapper")
@ServletComponentScan
public class SmartCommunityCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(SmartCommunityCareApplication.class, args);
    }
}
