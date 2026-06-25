package com.example.smartcommunity.controller;

import com.example.smartcommunity.dto.response.ApiResponse;
import com.example.smartcommunity.entity.Activity;
import com.example.smartcommunity.entity.ActivityParticipant;
import com.example.smartcommunity.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @PostMapping
    public ApiResponse<Activity> createActivity(@RequestBody Activity activity) {
        Activity created = activityService.createActivity(activity);
        return ApiResponse.success(created);
    }

    @GetMapping
    public ApiResponse<List<Activity>> getAllActivities() {
        List<Activity> activities = activityService.getAllActivities();
        return ApiResponse.success(activities);
    }

    @GetMapping("/active")
    public ApiResponse<List<Activity>> getActiveActivities() {
        List<Activity> activities = activityService.getActiveActivities();
        return ApiResponse.success(activities);
    }

    @GetMapping("/upcoming")
    public ApiResponse<List<Activity>> getUpcomingActivities() {
        List<Activity> activities = activityService.getUpcomingActivities();
        return ApiResponse.success(activities);
    }

    @GetMapping("/ended")
    public ApiResponse<List<Activity>> getEndedActivities() {
        List<Activity> activities = activityService.getEndedActivities();
        return ApiResponse.success(activities);
    }

    @GetMapping("/type/{type}")
    public ApiResponse<List<Activity>> getActivitiesByType(@PathVariable String type) {
        List<Activity> activities = activityService.getActivitiesByType(type);
        return ApiResponse.success(activities);
    }

    @GetMapping("/participant/{userId}")
    public ApiResponse<List<Activity>> getActivitiesByParticipant(@PathVariable Long userId) {
        List<Activity> activities = activityService.getActivitiesByParticipant(userId);
        return ApiResponse.success(activities);
    }

    @GetMapping("/{id}")
    public ApiResponse<Activity> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        return ApiResponse.success(activity);
    }

    @PutMapping("/{id}")
    public ApiResponse<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Activity updated = activityService.updateActivity(id, activity);
        return ApiResponse.success(updated);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ApiResponse.success("活动已删除");
    }

    @PostMapping("/{activityId}/register")
    public ApiResponse<ActivityParticipant> registerActivity(
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        ActivityParticipant participant = activityService.registerActivity(activityId, userId);
        if (participant == null) {
            return ApiResponse.error(400, "报名失败，可能已满或活动不存在");
        }
        return ApiResponse.success(participant);
    }

    @GetMapping("/{activityId}/participants")
    public ApiResponse<List<ActivityParticipant>> getParticipants(@PathVariable Long activityId) {
        List<ActivityParticipant> participants = activityService.getParticipants(activityId);
        return ApiResponse.success(participants);
    }

    @GetMapping("/{activityId}/participant/{userId}")
    public ApiResponse<Boolean> isParticipant(
            @PathVariable Long activityId,
            @PathVariable Long userId) {
        boolean result = activityService.isParticipant(activityId, userId);
        return ApiResponse.success(result);
    }

    @DeleteMapping("/{activityId}/register")
    public ApiResponse<String> unregisterActivity(
            @PathVariable Long activityId,
            @RequestParam Long userId) {
        boolean success = activityService.unregisterActivity(activityId, userId);
        if (success) {
            return ApiResponse.success("取消报名成功");
        }
        return ApiResponse.error(400, "取消报名失败");
    }
}