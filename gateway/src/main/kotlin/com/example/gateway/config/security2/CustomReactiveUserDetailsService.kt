package com.example.gateway.config.security2

import com.example.gateway.repository.UserRepository
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CustomReactiveUserDetailsService(private val userRepository: UserRepository) : ReactiveUserDetailsService {
    override fun findByUsername(username: String?): Mono<UserDetails> {
        return userRepository.findUserByEmail(username!!).cast(UserDetails::class.java)
    }
}
