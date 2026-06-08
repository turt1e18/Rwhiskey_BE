package com.turt1e18.rwhiskey.rwhiskey.api.config

import com.turt1e18.rwhiskey.rwhiskey.api.auth.security.CustomUserDetailService
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.csrf.CookieCsrfTokenRepository
import org.springframework.security.web.csrf.CsrfFilter
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
        configuration.allowCredentials = true // 프론트의 credentials: 'include' 허용 (세션 및 CSRF 쿠키 전송)
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        // Spring Security 6: SPA 대응을 위해 CSRF 지연 해제 핸들러 설정
        val requestHandler = CsrfTokenRequestAttributeHandler()
        requestHandler.setCsrfRequestAttributeName(null)

        http.httpBasic { it.disable() }
            .cors { it.configurationSource(corsConfigurationSource()) }
            .formLogin { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) }
            .authorizeHttpRequests {
                // OPTIONS(Preflight) 요청은 필터 통과를 위해 무조건 허용
                it.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                it.requestMatchers("/api/health/**", "/api/auth/**").permitAll()
                
                // CSRF가 비활성화된 경우(로컬 개발 등) 모든 요청 허용, 아니면 인증 필요
                if (!csrfEnabled) {
                    it.anyRequest().permitAll()
                } else {
                    it.anyRequest().authenticated()
                }
            }

        if (!csrfEnabled) {
            http.csrf { it.disable() }
        } else {
            // 프론트엔드 자바스크립트가 쿠키를 읽어 헤더에 세팅할 수 있도록 HttpOnly=false 강제
            val repository = CookieCsrfTokenRepository.withHttpOnlyFalse()

            // 운영 환경(turt1e18.work)일 경우 교차 서브도메인 쿠키 정책 강제 적용
            if (allowedOrigins.contains("turt1e18.work")) {
                repository.setCookieCustomizer { builder ->
                    builder.domain("turt1e18.work") // 세션 도메인과 일치시킴
                        .sameSite("None")        // HTTPS 교차 도메인 쿠키 전송 허용
                        .secure(true)            // Cloudflare 환경 필수
                        .path("/")
                }
            }

            http.csrf { csrf ->
                csrf.csrfTokenRepository(repository)
                    .csrfTokenRequestHandler(requestHandler)
                    .ignoringRequestMatchers("/api/auth/**")
            }
        }

        // 모든 요청에 대해 CSRF 토큰 생성을 강제하여 즉시 브라우저 쿠키에 반영되도록 하는 커스텀 필터
        // (기존 BasicAuthenticationFilter 대신 CsrfFilter 직후에 배치하여 타이밍 이슈 차단)
        http.addFilterAfter({ request, response, chain ->
            val token = request.getAttribute(CsrfToken::class.java.name) as? CsrfToken
            token?.token // 명시적으로 getter를 호출하여 생성을 트리거함
            chain.doFilter(request, response)
        }, CsrfFilter::class.java)

        return http.build()
    }
}