package com.example.smartcommunity.controller;

import com.example.smartcommunity.entity.User;
import com.example.smartcommunity.entity.UserRelation;
import com.example.smartcommunity.security.SecurityUtils;
import com.example.smartcommunity.service.UserRelationService;
import com.example.smartcommunity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/relations")
@RequiredArgsConstructor
public class UserRelationController {

    private final UserRelationService userRelationService;
    private final UserService userService;

    /** 绑定关系 — 仅管理员 */
    @PostMapping("/bind")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> bind(@RequestBody Map<String, Object> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Long elderId = Long.parseLong(body.get("elderId").toString());
            Long familyMemberId = Long.parseLong(body.get("familyMemberId").toString());
            String relationType = body.get("relationType") != null ? body.get("relationType").toString() : "child";

            UserRelation relation = userRelationService.createRelation(elderId, familyMemberId, relationType);
            response.put("code", 200);
            response.put("message", "绑定成功");
            response.put("data", relation);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "绑定失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /** 解绑关系 — 仅管理员 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> unbind(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            userRelationService.deleteRelation(id);
            response.put("code", 200);
            response.put("message", "解绑成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "解绑失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /** 查询某老人的家属 — 管理员或该老人本人或其家属 */
    @GetMapping("/elder/{elderId}")
    @PreAuthorize("hasRole('ADMIN') or #elderId == T(com.example.smartcommunity.security.SecurityUtils).getCurrentUserId()")
    public ResponseEntity<Map<String, Object>> getByElder(@PathVariable Long elderId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserRelation> relations = userRelationService.getRelationsByElder(elderId);
            List<Map<String, Object>> result = buildRelationList(relations);
            response.put("code", 200);
            response.put("data", result);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /** 查询某家属关联的老人 — 管理员或该家属本人 */
    @GetMapping("/family/{familyMemberId}")
    @PreAuthorize("hasRole('ADMIN') or #familyMemberId == T(com.example.smartcommunity.security.SecurityUtils).getCurrentUserId()")
    public ResponseEntity<Map<String, Object>> getByFamilyMember(@PathVariable Long familyMemberId) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<UserRelation> relations = userRelationService.getRelationsByFamilyMember(familyMemberId);
            List<Map<String, Object>> result = buildRelationList(relations);
            response.put("code", 200);
            response.put("data", result);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /** 检查关系是否存在 — 仅管理员 */
    @GetMapping("/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> exists(@RequestParam Long elderId, @RequestParam Long familyMemberId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = userRelationService.existsRelation(elderId, familyMemberId);
            response.put("code", 200);
            response.put("data", exists);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    /** 清理重复关系 — 仅管理员 */
    @DeleteMapping("/clean-duplicates")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> cleanDuplicates() {
        Map<String, Object> response = new HashMap<>();
        try {
            userRelationService.cleanDuplicateRelations();
            response.put("code", 200);
            response.put("message", "清理成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "清理失败: " + e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    // ==================== 私有辅助方法 ====================

    private List<Map<String, Object>> buildRelationList(List<UserRelation> relations) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (UserRelation relation : relations) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", relation.getId());
            item.put("elderId", relation.getElderId());
            item.put("familyMemberId", relation.getFamilyMemberId());
            item.put("relationType", relation.getRelationType());

            User elder = userService.getUserById(relation.getElderId());
            if (elder != null) {
                item.put("elderName", elder.getName());
                item.put("elderPhone", elder.getPhone());
            }

            User familyMember = userService.getUserById(relation.getFamilyMemberId());
            if (familyMember != null) {
                item.put("familyMemberName", familyMember.getName());
                item.put("familyMemberPhone", familyMember.getPhone());
            }

            result.add(item);
        }
        return result;
    }
}
