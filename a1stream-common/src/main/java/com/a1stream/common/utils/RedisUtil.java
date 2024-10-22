package com.a1stream.common.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    /**
     * 默认过时时间
     */
    @SuppressWarnings("unused")
    private static final int DEFAULT_EXPRIE_TIME = 3600*24;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    public String get(String key) {

        return stringRedisTemplate.opsForValue().get(key);
    }

    public Long getExpire(String key) {

    	return stringRedisTemplate.boundHashOps(key).getExpire();
    }

    public boolean set(String key, String value, long exp) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, exp, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean set(String key, String value) {
        try {
            stringRedisTemplate.opsForValue().set(key, value);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean delete(String key) {
        try {
            stringRedisTemplate.delete(key);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public Set<String> keys(String key) {
        return stringRedisTemplate.keys(key);
    }
}