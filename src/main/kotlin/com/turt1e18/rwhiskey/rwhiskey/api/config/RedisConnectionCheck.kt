package com.turt1e18.rwhiskey.rwhiskey.api.config

import jakarta.annotation.PostConstruct
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Component

@Component
class RedisConnectionCheck(
    private val redisTemplate : StringRedisTemplate
) {
    @PostConstruct
    fun check(){
        redisTemplate.opsForValue().set("redis:test","ok")
        val value = redisTemplate.opsForValue().get("redis:test")
        println("Redis connection check result: $value")

    }
}