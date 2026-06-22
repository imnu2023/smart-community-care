package com.example.smartcommunity.service.impl;

import com.example.smartcommunity.entity.CommunityService;
import com.example.smartcommunity.mapper.CommunityServiceMapper;
import com.example.smartcommunity.service.CommunityServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityServiceInterface {
    
    private final CommunityServiceMapper communityServiceMapper;
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public CommunityService addService(CommunityService service) {
        service.setStatus("active");
        service.setCreatedAt(LocalDateTime.now());
        service.setUpdatedAt(LocalDateTime.now());
        communityServiceMapper.insert(service);
        return service;
    }
    
    @Override
    public CommunityService updateService(CommunityService service) {
        service.setUpdatedAt(LocalDateTime.now());
        communityServiceMapper.updateById(service);
        return service;
    }
    
    @Override
    public void deleteService(Long id) {
        communityServiceMapper.deleteById(id);
    }
    
    @Override
    public List<CommunityService> getAllServices() {
        return communityServiceMapper.findAllActive();
    }
    
    @Override
    public List<CommunityService> getServicesByType(String type) {
        return communityServiceMapper.findByType(type);
    }
    
    @Override
    public CommunityService getServiceById(Long id) {
        return communityServiceMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public void cleanDuplicateServices() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        communityServiceMapper.deleteDuplicateServices();
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
    }
    
    @Override
    @Transactional
    public void fixGarbledServices() {
        jdbcTemplate.update("UPDATE community_service SET service_name = '家政服务', description = '为社区居民提供上门家政服务，包括保洁、做饭、洗衣等', provider = '阳光家政服务中心' WHERE service_type = 'housekeeping'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '医疗服务', description = '为社区居民提供医疗服务，包括体检、问诊、康复等', provider = '社区医院' WHERE service_type = 'medical'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '生活服务', description = '日常生活便利服务，包括代购、送水、送餐等', provider = '社区服务中心' WHERE service_type = 'life'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '理发服务', description = '专业理发师上门理发服务，为老人提供便捷的理发体验', provider = '时尚理发店' WHERE service_type = 'beauty'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '送餐服务', description = '营养均衡的餐食配送服务，为行动不便的老人提供方便', provider = '健康餐饮中心' WHERE service_type = 'food'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '康复护理', description = '专业康复护理服务，帮助老人进行身体康复训练', provider = '康复中心' WHERE service_type = 'health'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '代购服务', description = '为老人代购日常用品、药品等，送货上门', provider = '社区代购服务' WHERE service_type = 'shopping'");
        jdbcTemplate.update("UPDATE community_service SET service_name = '陪伴服务', description = '为独居老人提供陪伴聊天、散步等服务', provider = '志愿者服务中心' WHERE service_type = 'companion'");
    }
    
    @Override
    @Transactional
    public void cleanAllGarbledData() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=0");
        
        jdbcTemplate.update("DELETE FROM activity WHERE id > 5");
        
        jdbcTemplate.update("DELETE FROM activity_participant WHERE activity_id > 5");
        
        jdbcTemplate.update("DELETE FROM community_service WHERE id > 3");
        
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS=1");
        
        insertAdditionalServices();
    }
    
    private void insertAdditionalServices() {
        String[] serviceNames = {"理发服务", "送餐服务", "康复护理", "代购服务", "陪伴服务"};
        String[] serviceTypes = {"beauty", "food", "health", "shopping", "companion"};
        String[] descriptions = {
            "专业理发师上门理发服务，为老人提供便捷的理发体验",
            "营养均衡的餐食配送服务，为行动不便的老人提供方便",
            "专业康复护理服务，帮助老人进行身体康复训练",
            "为老人代购日常用品、药品等，送货上门",
            "为独居老人提供陪伴聊天、散步等服务"
        };
        String[] providers = {"时尚理发店", "健康餐饮中心", "康复中心", "社区代购服务", "志愿者服务中心"};
        double[] prices = {50.0, 30.0, 120.0, 15.0, 40.0};
        String[] phones = {"13822223333", "13833334444", "13844445555", "13855556666", "13866667777"};
        
        for (int i = 0; i < serviceNames.length; i++) {
            jdbcTemplate.update(
                "INSERT INTO community_service (service_name, service_type, description, price, provider, phone, status, sort_order, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'active', ?, NOW(), NOW())",
                serviceNames[i], serviceTypes[i], descriptions[i], prices[i], providers[i], phones[i], i + 4
            );
        }
    }
    
    @Override
    @Transactional
    public void fixAllGarbledData() {
        fixGarbledServices();
        fixGarbledActivities();
        fixGarbledUsers();
        fixGarbledRelations();
    }
    
    private void fixGarbledActivities() {
        jdbcTemplate.update("UPDATE activity SET title = '书法艺术交流', description = '邀请社区书法爱好者一起交流书法技艺，互学互鉴，共同提高书法水平', type = 'culture', location = '社区活动中心三楼', organizer = '书画社' WHERE id = 1");
        jdbcTemplate.update("UPDATE activity SET title = '广场舞健身活动', description = '每天早晨的广场舞健身活动，强健体魄，陶冶情操', type = 'sports', location = '社区广场', organizer = '健身管理办公室' WHERE id = 2");
        jdbcTemplate.update("UPDATE activity SET title = '智能手机使用培训', description = '帮助老年人学习智能手机的使用，包括微信使用、视频通话、健康监测', type = 'study', location = '社区活动中心二楼', organizer = '社区志愿者' WHERE id = 3");
        jdbcTemplate.update("UPDATE activity SET title = '健康养生讲座', description = '邀请专业医生讲解老年人健康养生知识，解答健康疑问', type = 'health', location = '社区活动中心三楼大厅', organizer = '社区服务中心' WHERE id = 4");
        jdbcTemplate.update("UPDATE activity SET title = '社区生日聚会', description = '为当月过生日的居民举办生日聚会，共度欢乐时光', type = 'social', location = '社区活动中心', organizer = '社区管理办公室' WHERE id = 5");
    }
    
    private void fixGarbledUsers() {
        jdbcTemplate.update("UPDATE sys_user SET name = '管理员', username = 'admin', phone = '13800138000', address = '社区管理办公室' WHERE id = 1");
        jdbcTemplate.update("UPDATE sys_user SET name = '张大爷', username = 'elder1', phone = '13900139000', address = '幸福小区3号楼201室' WHERE id = 2");
        jdbcTemplate.update("UPDATE sys_user SET name = '张小红', username = 'family1', phone = '13700137000', address = '幸福小区5号楼302室' WHERE id = 3");
        jdbcTemplate.update("UPDATE sys_user SET name = '王阿姨', username = 'elder2', phone = '13600136000', address = '阳光小区2号楼101室' WHERE id = 4");
        jdbcTemplate.update("UPDATE sys_user SET name = '李阿姨', username = 'elder3', phone = '13500135000', address = '和谐小区1号楼402室' WHERE id = 5");
    }
    
    private void fixGarbledRelations() {
        jdbcTemplate.update("UPDATE user_relation SET relation_type = 'child' WHERE relation_type NOT IN ('child', 'parent', 'spouse', 'sibling')");
    }
}
