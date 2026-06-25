package com.example.smartcommunity.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 存储在 Authentication.details 中的用户身份信息
 *
 * <p>业务代码可通过以下方式获取当前用户：
 * <pre>{@code
 *   Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 *   if (auth.getDetails() instanceof JwtUserDetails details) {
 *       Long currentUserId = details.getUserId();
 *       Integer currentRole = details.getRole();
 *   }
 * }</pre>
 */
@Getter
@AllArgsConstructor
public class JwtUserDetails {
    private final Long userId;
    private final String username;
    private final Integer role;
}
