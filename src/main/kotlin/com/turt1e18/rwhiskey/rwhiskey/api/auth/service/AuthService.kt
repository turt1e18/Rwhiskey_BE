package com.turt1e18.rwhiskey.rwhiskey.api.auth.service

import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.request.SignupRequest
import com.turt1e18.rwhiskey.rwhiskey.api.auth.dto.response.SignupResponse
import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.User
import com.turt1e18.rwhiskey.rwhiskey.api.user.entity.UserToken
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserRepository
import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserTokenRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val userTokenRepository: UserTokenRepository,
) {
    @Transactional
    fun signUp(request: SignupRequest): SignupResponse {
        // 회원 이메일 중복검사
        if (userRepository.existsByEmail(request.email)){
            throw IllegalArgumentException("Email already in use")
        }
        // 비밀번호 인코딩
        val encodedPassword = passwordEncoder.encode(request.password)
        // 사용자 저장
        val saveUser = userRepository.save(
            User(
                email = request.email,
                name = request.name,
                pw = encodedPassword,
                type = 0
            )
        )
        //일일 사용량에 사용자 추가
        userTokenRepository.save(
            UserToken(
                uid = saveUser.uid!!,
                dailyLimit = 10,
                usedCount = 0,
                lastResetTime = LocalDate.now()
            )
        )
        return SignupResponse(
            uid = saveUser.uid!!,
            email = saveUser.email,
            name = saveUser.name,
            message = "회원가입 완료",
        )
    }
}