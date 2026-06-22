package com.example.smartcommunity.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 插件配置
 *
 * 启用功能：
 * - 乐观锁 (@Version) — 解决并发更新冲突
 * - 分页插件 — 替代 PageHelper（可选，如不启用则注释掉）
 * - 逻辑删除 (@TableLogic) — 通过注解自动生效，无需额外拦截器
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        // 1. 乐观锁拦截器：配合实体类 @Version 注解
        //    更新时自动在 WHERE 条件追加 version = ? 并将 version + 1
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());

        // 2. 分页插件（如需启用，取消注释）
        //    注意：项目中已引入 PageHelper，两者可能冲突
        //    建议择一使用，推荐逐步迁移到 MyBatis-Plus 分页
        // interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
