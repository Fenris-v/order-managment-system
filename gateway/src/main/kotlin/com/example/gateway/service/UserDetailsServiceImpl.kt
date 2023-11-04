package com.example.gateway.service

import com.example.gateway.model.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailsServiceImpl : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        // todo
        // val userDto = UserDto(1L,"admin", passwordEncoder.encode("password"))
        return User(1L, "admin@mail.com", "password")
    }
}
