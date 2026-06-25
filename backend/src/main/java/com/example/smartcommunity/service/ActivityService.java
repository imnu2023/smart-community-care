package com.example.smartcommunity.service;

import com.example.smartcommunity.entity.Activity;
import com.example.smartcommunity.entity.ActivityParticipant;

import java.util.List;

public interface ActivityService {

    Activity createActivity(Activity activity);

    Activity getActivityById(Long id);

    List<Activity> getAllActivities();

    List<Activity> getActiveActivities();

    List<Activity> getActivitiesByType(String type);

    List<Activity> getUpcomingActivities();

    List<Activity> getEndedActivities();

    List<Activity> getActivitiesByParticipant(Long userId);

    Activity updateActivity(Long id, Activity activity);

    void deleteActivity(Long id);

    ActivityParticipant registerActivity(Long activityId, Long userId);

    List<ActivityParticipant> getParticipants(Long activityId);

    boolean isParticipant(Long activityId, Long userId);

    boolean unregisterActivity(Long activityId, Long userId);
}