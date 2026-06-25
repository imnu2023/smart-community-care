package com.example.smartcommunity.config;

import com.example.smartcommunity.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Spring Security 安全配置
 *
 * <p>认证策略：JWT 无状态令牌（由 JwtAuthenticationFilter 解析）
 * <p>授权策略：URL 级别（此处）+ 方法级别（@PreAuthorize 注解）双层控制
 * <p>角色映射：1=ROLE_ADMIN / 2=ROLE_ELDER / 3=ROLE_FAMILY
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)  // 开启 @PreAuthorize / @PostAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /** ==================== 公共端点白名单 ==================== */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/captcha",
            "/api/auth/hash/**",
            // 文件上传（健康报告、头像等）
            "/api/upload/**",
            // 健康检查、静态资源等
            "/actuator/**",
            "/error"
    };

    /** ==================== 管理员专属端点 ==================== */
    private static final String[] ADMIN_ENDPOINTS = {
            "/api/admin/**"
    };

    // ==================== SecurityFilterChain ====================

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 前后端分离，禁用 CSRF
            .csrf().disable()

            // 无状态会话 — 不创建 HttpSession
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            // ========== URL 级别授权规则 ==========
            .and()
            .authorizeRequests()
                // 公共端点：无需认证
                .antMatchers(PUBLIC_ENDPOINTS).permitAll()
                // 管理员端点：仅 ROLE_ADMIN
                .antMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                // 其余所有 /api/** 端点：需要已认证（持有有效 JWT）
                .antMatchers("/api/**").authenticated()
                // 其他请求（静态资源等）：放行
                .anyRequest().permitAll()

            // ========== 异常处理 ==========
            .and()
            .exceptionHandling()
                // 未登录 → 401
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                // 权限不足 → 403
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                        "{\"code\":403,\"message\":\"权限不足，请联系管理员\"}"
                    );
                })

            // ========== CORS ==========
            .and()
            .cors()

            // ========== JWT 过滤器 ==========
            // 在 UsernamePasswordAuthenticationFilter 之前插入，确保先解析 JWT
            .and()
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ==================== Bean 定义 ====================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsFilter corsFilter() {
        return new CorsFilter(corsConfigurationSource());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
