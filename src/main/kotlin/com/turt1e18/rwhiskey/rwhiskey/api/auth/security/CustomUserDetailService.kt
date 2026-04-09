package com.turt1e18.rwhiskey.rwhiskey.api.auth.security

import com.turt1e18.rwhiskey.rwhiskey.api.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

@Service
class CustomUserDetailService(private val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmailAndDeleteDateIsNull(username)
            ?: throw UsernameNotFoundException("사용자를 찾을 수 없습니다.")
        return CustomUserPrincipal(
            uid = user.uid!!,
            emailValue = user.email,
            name = user.name,
            passwordValue = user.pw
        )
    }
}