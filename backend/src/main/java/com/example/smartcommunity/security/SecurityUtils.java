package com.example.smartcommunity.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全上下文工具类 — 便捷获取当前登录用户信息
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /** 获取当前登录用户的 userId，未登录返回 null */
    public static Long getCurrentUserId() {
        JwtUserDetails details = getDetails();
        return details != null ? details.getUserId() : null;
    }

    /** 获取当前登录用户的 username，未登录返回 null */
    public static String getCurrentUsername() {
        JwtUserDetails details = getDetails();
        return details != null ? details.getUsername() : null;
    }

    /** 获取当前登录用户的 role，未登录返回 null */
    public static Integer getCurrentRole() {
        JwtUserDetails details = getDetails();
        return details != null ? details.getRole() : null;
    }

    /** 当前请求是否已认证 */
    public static boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated()
                && auth.getDetails() instanceof JwtUserDetails;
    }

    private static JwtUserDetails getDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getDetails() instanceof JwtUserDetails) {
            return (JwtUserDetails) auth.getDetails();
        }
        return null;
    }
}
