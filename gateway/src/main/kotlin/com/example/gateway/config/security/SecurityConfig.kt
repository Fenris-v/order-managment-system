package com.example.gateway.config.security

import com.example.gateway.config.filter.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig(
//    private val authenticationProvider: AuthenticationProvider,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
////        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
//        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
//    }

    @Bean
    fun getPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(8)
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
//        return httpSecurity.getSharedObject(AuthenticationManagerBuilder::class.java)
//            .authenticationProvider(authenticationProvider)
//            .build()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { csrf -> csrf.disable() } // todo: включить потом цсрф
//            .cors(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            .anonymous(Customizer.withDefaults())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

//        httpSecurity.oauth2Login { oauth -> oauth.loginPage("/login").successHandler(null) }

//        http.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

//        httpSecurity.authenticationProvider(authenticationProvider)
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
