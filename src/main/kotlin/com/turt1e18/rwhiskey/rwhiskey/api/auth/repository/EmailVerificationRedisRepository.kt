package com.turt1e18.rwhiskey.rwhiskey.api.auth.repository

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class EmailVerificationRedisRepository(
    private val redisTemplate: StringRedisTemplate
){
  companion object{
      private const val CODE_PREFIX ="email:verify:code:"
      private const val VERIFY_PREFIX ="email:verify:verified:"
  }
    fun saveCode(email: String,codeHash: String) {
        redisTemplate.opsForValue().set(CODE_PREFIX + email,codeHash, Duration.ofMinutes(5))
    }

    fun getCode(email: String):String?{
        return redisTemplate.opsForValue().get(CODE_PREFIX + email)
    }

    fun deleteCode(email: String){
        redisTemplate.delete(CODE_PREFIX + email)
    }

    fun getCodeHash(email: String):String?{
        return redisTemplate.opsForValue().get(CODE_PREFIX + email)
    }

    fun saveVerified(email:String){
        redisTemplate.opsForValue().set(VERIFY_PREFIX + email, "true", Duration.ofMinutes(30))
    }

    fun isVerified(email: String): Boolean{
        return redisTemplate.opsForValue().get(VERIFY_PREFIX + email) == "true"
    }

    fun deleteVerified(email: String){
        redisTemplate.delete(VERIFY_PREFIX + email)
    }
}