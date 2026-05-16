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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler
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
        // CSRF 토큰을 지연 없이 즉시 쿠키로 처리하기 위한 핸들러
        val requestHandler = CsrfTokenRequestAttributeHandler()
        requestHandler.setCsrfRequestAttributeName(null) 

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
            // 운영 및 로컬 환경 모두에서 프론트엔드(fetch/axios)가 토큰을 읽고 즉시 사용할 수 있도록 설정
            http.csrf { csrf ->
                csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(requestHandler)
                    .ignoringRequestMatchers("/api/auth/**")
            }
        }

        // 모든 요청에 대해 CSRF 토큰을 명시적으로 해결하여 쿠키에 즉시 반영되도록 보장하는 필터
        http.addFilterAfter({ request, response, chain ->
            val token = request.getAttribute(CsrfToken::class.java.name) as? CsrfToken
            token?.token // 명시적으로 getter를 호출하여 생성을 트리거함
            chain.doFilter(request, response)
        }, BasicAuthenticationFilter::class.java)

        return http.build()
    }
}