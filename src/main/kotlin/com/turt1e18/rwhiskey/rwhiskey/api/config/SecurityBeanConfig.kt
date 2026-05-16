package com.turt1e18.rwhiskey.rwhiskey.api.config

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserDetailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
class SecurityBeanConfig(
    private val customUserDetailService: CustomUserDetailService,
    @param:Value("\${app.security.csrf.enabled:true}") private val csrfEnabled: Boolean,
    @param:Value("\${app.cors.allowed-origins}") private val allowedOrigins: String
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun authenticationProvider(): DaoAuthenticationProvider {
        val provider = DaoAuthenticationProvider(customUserDetailService)
        provider.setPasswordEncoder(passwordEncoder())
        return provider
    }

    @Bean
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()
        configuration.allowedOrigins = allowedOrigins.split(",")
        configuration.allowedMethods = listOf("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true // 세션 쿠키 허용
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.httpBasic { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests {
                it.requestMatchers("/api/health/**", "/api/auth/**").permitAll()
                it.anyRequest().authenticated()
            }

        if (!csrfEnabled) {
            http.csrf { it.disable() }
        } else {
            // 운영 환경에서는 CSRF 활성화하되, 프론트엔드가 토큰을 읽을 수 있도록 Cookie 방식 적용
            http.csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .ignoringRequestMatchers("/api/auth/**")
            }
        }

        return http.build()
    }
}