package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.User;
import com.example.smartcommunity.security.SecurityUtils;
import com.example.smartcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 获取所有用户 — 仅管理员 */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ApiResponse.success("success", users);
    }

    /** 获取单个用户 — 管理员或本人 */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.example.smartcommunity.security.SecurityUtils).getCurrentUserId()")
    public ApiResponse<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ApiResponse.success(user);
    }

    /** 更新用户信息 — 管理员或本人 */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == T(com.example.smartcommunity.security.SecurityUtils).getCurrentUserId()")
    public ApiResponse<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return ApiResponse.success("更新成功", user);
    }

    /** 删除用户 — 仅管理员 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ApiResponse.success("删除成功");
    }
}
