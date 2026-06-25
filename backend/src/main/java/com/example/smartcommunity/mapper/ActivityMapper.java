package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.Activity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

    @Select("SELECT * FROM activity WHERE status = 'active' AND start_time <= NOW() AND (end_time IS NULL OR end_time >= NOW()) ORDER BY start_time DESC")
    List<Activity> findActiveActivities();

    @Select("SELECT * FROM activity WHERE type = #{type} AND status = 'active' ORDER BY start_time DESC")
    List<Activity> findByType(String type);

    @Select("SELECT * FROM activity WHERE start_time > NOW() AND status = 'active' ORDER BY start_time ASC")
    List<Activity> findUpcoming();

    @Select("SELECT * FROM activity WHERE end_time < NOW() AND status = 'active' ORDER BY end_time DESC")
    List<Activity> findEnded();

    @Select("SELECT a.* FROM activity a WHERE a.id IN (SELECT ap.activity_id FROM activity_participant ap WHERE ap.user_id = #{userId}) ORDER BY a.start_time DESC")
    List<Activity> findByParticipant(Long userId);
}