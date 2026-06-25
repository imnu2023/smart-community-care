package com.example.smartcommunity.security;

import com.example.smartcommunity.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * JWT 身份验证过滤器 — 在每个请求中解析 Token 并设置安全上下文
 *
 * <p>职责链位置：在 Spring Security Filter Chain 中，位于所有授权过滤器之前。
 * <p>工作流程：
 * <ol>
 *   <li>从 Authorization 请求头提取 Bearer Token</li>
 *   <li>调用 JwtUtil.verifyToken() 验签 + 校验过期</li>
 *   <li>从 Token Claims 提取 userId / username / role</li>
 *   <li>构建 UsernamePasswordAuthenticationToken 并设置到 SecurityContext</li>
 * </ol>
 * <p>Token 缺失或无效时：不阻断请求，交由后续 SecurityConfig 的 URL 授权规则处理。
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /** 角色 → Spring Security 权限字符串映射 */
    private static String toAuthority(Integer role) {
        if (role == null) return "ROLE_ELDER"; // 兜底
        switch (role) {
            case 1:  return "ROLE_ADMIN";
            case 2:  return "ROLE_ELDER";
            case 3:  return "ROLE_FAMILY";
            default: return "ROLE_ELDER";
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token == null) {
            // 无 Token → 放行，由 SecurityConfig 的 URL 规则判断是否拒绝
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 验签 + 检查过期
            com.auth0.jwt.interfaces.DecodedJWT decoded = jwtUtil.verifyToken(token);

            Long userId = decoded.getClaim("userId").asLong();
            String username = decoded.getClaim("username").asString();
            Integer role = decoded.getClaim("role").asInt();

            // 构建 Spring Security 权限
            List<SimpleGrantedAuthority> authorities =
                    Collections.singletonList(new SimpleGrantedAuthority(toAuthority(role)));

            // 构建认证令牌（已认证状态）
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
            // 将 username 和 role 存入 details，方便后续业务取用
            authentication.setDetails(new JwtUserDetails(userId, username, role));

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("JWT 认证成功: userId={}, username={}, role={}", userId, username, role);

        } catch (Exception e) {
            // Token 无效 / 过期 / 签名不匹配 → 清空上下文，放行
            // 若请求的是受保护资源，后续 SecurityConfig 会返回 401/403
            SecurityContextHolder.clearContext();
            log.debug("JWT 认证失败: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /** 从请求头提取 Bearer Token */
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
