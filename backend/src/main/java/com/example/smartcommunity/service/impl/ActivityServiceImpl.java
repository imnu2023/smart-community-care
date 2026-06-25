package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.Activity;
import com.example.smartcommunity.entity.ActivityParticipant;
import com.example.smartcommunity.exception.BusinessException;
import com.example.smartcommunity.mapper.ActivityMapper;
import com.example.smartcommunity.mapper.ActivityParticipantMapper;
import com.example.smartcommunity.service.ActivityService;
import com.example.smartcommunity.service.NotificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityParticipantMapper participantMapper;

    @Autowired
    private NotificationHelper notificationHelper;

    @Override
    @Transactional
    public Activity createActivity(Activity activity) {
        activity.setCurrentParticipants(0);
        activity.setStatus("active");
        activity.setCreatedAt(LocalDateTime.now());
        if (activity.getEndTime() == null && activity.getStartTime() != null) {
            activity.setEndTime(activity.getStartTime().plusHours(2));
        }
        activityMapper.insert(activity);
        return activity;
    }

    @Override
    public Activity getActivityById(Long id) {
        return activityMapper.selectById(id);
    }

    @Override
    public List<Activity> getAllActivities() {
        return activityMapper.selectList(null);
    }

    @Override
    public List<Activity> getActiveActivities() {
        return activityMapper.findActiveActivities();
    }

    @Override
    public List<Activity> getActivitiesByType(String type) {
        return activityMapper.findByType(type);
    }

    @Override
    public List<Activity> getUpcomingActivities() {
        return activityMapper.findUpcoming();
    }

    @Override
    public List<Activity> getEndedActivities() {
        return activityMapper.findEnded();
    }

    @Override
    public List<Activity> getActivitiesByParticipant(Long userId) {
        return activityMapper.findByParticipant(userId);
    }

    @Override
    @Transactional
    public Activity updateActivity(Long id, Activity activity) {
        Activity existing = activityMapper.selectById(id);
        if (existing != null) {
            existing.setTitle(activity.getTitle());
            existing.setDescription(activity.getDescription());
            existing.setType(activity.getType());
            existing.setLocation(activity.getLocation());
            existing.setStartTime(activity.getStartTime());
            existing.setEndTime(activity.getEndTime());
            existing.setMaxParticipants(activity.getMaxParticipants());
            existing.setOrganizer(activity.getOrganizer());
            existing.setStatus(activity.getStatus());
            activityMapper.updateById(existing);
        }
        return existing;
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        activityMapper.deleteById(id);
    }

    @Override
    @Transactional
    public ActivityParticipant registerActivity(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null || !"active".equals(activity.getStatus())) {
            return null;
        }

        ActivityParticipant existing = participantMapper.findByActivityAndUser(activityId, userId);
        if (existing != null) {
            return existing;
        }

        if (activity.getCurrentParticipants() >= activity.getMaxParticipants()) {
            return null;
        }

        ActivityParticipant participant = new ActivityParticipant();
        participant.setActivityId(activityId);
        participant.setUserId(userId);
        participant.setStatus("confirmed");
        participant.setRegisteredAt(LocalDateTime.now());
        participantMapper.insert(participant);

        activity.setCurrentParticipants(activity.getCurrentParticipants() + 1);
        int rows = activityMapper.updateById(activity);
        if (rows == 0) {
            throw new BusinessException("报名并发冲突，请重试");
        }

        notificationHelper.send(userId, NotificationHelper.TYPE_ACTIVITY,
                "「" + activity.getTitle() + "」报名成功，活动时间 "
                + activity.getStartTime().format(DateTimeFormatter.ofPattern("M月d日 HH:mm")));

        return participant;
    }

    @Override
    public List<ActivityParticipant> getParticipants(Long activityId) {
        return participantMapper.findByActivityId(activityId);
    }

    @Override
    public boolean isParticipant(Long activityId, Long userId) {
        ActivityParticipant participant = participantMapper.findByActivityAndUser(activityId, userId);
        return participant != null && "confirmed".equals(participant.getStatus());
    }

    @Override
    @Transactional
    public boolean unregisterActivity(Long activityId, Long userId) {
        Activity activity = activityMapper.selectById(activityId);
        if (activity == null) {
            return false;
        }

        ActivityParticipant participant = participantMapper.findByActivityAndUser(activityId, userId);
        if (participant == null || !"confirmed".equals(participant.getStatus())) {
            return false;
        }

        participantMapper.deleteById(participant.getId());

        activity.setCurrentParticipants(Math.max(0, activity.getCurrentParticipants() - 1));
        int rows = activityMapper.updateById(activity);
        if (rows == 0) {
            throw new BusinessException("取消报名并发冲突，请重试");
        }

        return true;
    }
}