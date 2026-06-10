package com.example.smartcommunity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.smartcommunity.entity.CommunityService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommunityServiceMapper extends BaseMapper<CommunityService> {  

    @Select("SELECT * FROM community_service WHERE service_type = #{serviceType}")
    List<CommunityService> findByType(String serviceType);

    @Select("SELECT * FROM community_service WHERE status = 'active'")
    List<CommunityService> findAllActive();
}