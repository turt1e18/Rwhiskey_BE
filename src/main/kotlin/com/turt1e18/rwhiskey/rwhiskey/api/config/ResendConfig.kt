package com.turt1e18.rwhiskey.rwhiskey.api.config

import com.resend.Resend
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ResendConfig {

    @Value("\${resend.api.key}")
    private  lateinit var key: String

    @Bean
    fun resend(): Resend{
        return Resend(key)
    }
}