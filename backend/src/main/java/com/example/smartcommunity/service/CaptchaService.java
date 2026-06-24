package com.example.smartcommunity.service;

import com.example.smartcommunity.exception.BusinessException;
import com.wf.captcha.SpecCaptcha;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final String CAPTCHA_KEY_PREFIX = "CAPTCHA:";
    private static final long CAPTCHA_EXPIRE_MINUTES = 2;

    /**
     * 生成图形验证码并存入 Redis
     * @return uuid + base64 图片
     */
    public Map<String, String> generateCaptcha() {
        // 生成验证码图片：宽130, 高48, 4位字符
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);
        String verCode = specCaptcha.text().toLowerCase();

        // 生成 UUID 作为存入 Redis 的唯一 Key
        String uuid = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(
                CAPTCHA_KEY_PREFIX + uuid,
                verCode,
                CAPTCHA_EXPIRE_MINUTES,
                TimeUnit.MINUTES
        );

        // 封装返回给前端的数据
        Map<String, String> data = new HashMap<>();
        data.put("uuid", uuid);
        data.put("captchaImg", specCaptcha.toBase64());
        return data;
    }

    /**
     * 校验用户输入的验证码 —— 阅后即焚，无论正确与否，看过一次就销毁
     * @param uuid  前端传来的验证码唯一标识
     * @param code  用户输入的验证码
     */
    public void validate(String uuid, String code) {
        String redisKey = CAPTCHA_KEY_PREFIX + uuid;
        String storedCode = stringRedisTemplate.opsForValue().get(redisKey);

        if (storedCode == null) {
            throw new BusinessException("验证码已过期，请重新获取");
        }

        // 阅后即焚：先校验，通过后再删除
        if (!storedCode.equalsIgnoreCase(code.trim())) {
            stringRedisTemplate.delete(redisKey);
            throw new BusinessException("验证码错误");
        }

        stringRedisTemplate.delete(redisKey);
    }
}
