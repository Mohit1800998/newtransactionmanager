package com.rapipay.NewTransactionManager.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class RedisUtil {

    @Autowired
    private RedisTemplate< String, Object > redisTemplate;

    public Object getValue( final String key ) {
        return redisTemplate.opsForValue().get( key );
    }

    public Object setValue( final String key, final String value ) {
        redisTemplate.opsForValue().set( key, value );
        return redisTemplate.opsForValue().get( key );
    }

    public Object setValue( final String key, final String value, final Duration time ) {
        redisTemplate.opsForValue().set( key, value, time );
        return redisTemplate.opsForValue().get( key );
    }


    public List<Object> getValue( final List<String> key ) {
        return redisTemplate.opsForValue().multiGet( key );
    }


    public Boolean setValue( final Map<String,String> value ) {

        redisTemplate.opsForValue().multiSet(value);

        return true;
    }


}