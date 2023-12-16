package com.example.gateway.config.security2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.web.reactive.config.WebFluxConfigurer

@Configuration
@EnableWebFluxSecurity
class SecurityConfig : WebFluxConfigurer {
    companion object {
        val EXCLUDED_PATHS = arrayOf("/login", "/register", "/logout")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
    }

    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
        jwtAuthenticationFilter: Authent
    ): SecurityWebFilterChain {
        http
            .csrf { it.disable() }// todo: включить потом цсрф
            .cors(Customizer.withDefaults()) // todo: настроить потом
            .httpBasic { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            .formLogin { it.disable() }
            .addFilterAt()
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .authorizeExchange {
                it.pathMatchers(*EXCLUDED_PATHS).permitAll()
                it.anyExchange().authenticated()
            }

        return http.build()
    }
}
