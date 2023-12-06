package com.example.gateway.config.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
class SecurityConfig {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
    }

//    @Bean
//    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
//        return config.authenticationManager
////        return httpSecurity.getSharedObject(AuthenticationManagerBuilder::class.java)
////            .authenticationProvider(authenticationProvider)
////            .build()
//    }

//    @Bean
//    fun authenticationManager(): ReactiveAuthenticationManager {
//        return CustomAuthenticationManager()
//    }
//
//    @Bean
//    fun securityContextRepository(): ServerSecurityContextRepository {
//        return CustomSecurityContextRepository()
//    }

    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
        authenticationManager: CustomAuthenticationManager,
        securityContextRepository: CustomSecurityContextRepository
    ): SecurityWebFilterChain {
        http
            .csrf { csrf -> csrf.disable() } // todo: включить потом цсрф
            .cors(Customizer.withDefaults())
//            .cors { it.disable() }
//            .httpBasic(Customizer.withDefaults())
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .authenticationManager(authenticationManager)
            .securityContextRepository(securityContextRepository)
//            .formLogin { http -> http.loginPage() }
//            .authorizeHttpRequests { auth -> auth.anyRequest().permitAll() }
            .authorizeExchange {
                it
                    .pathMatchers("/api/v1/user/register", "/api/v1/user/login", "/actuator/**")
                    .permitAll()
                    .anyExchange()
                    .authenticated()
            }
//            .anonymous(Customizer.withDefaults())
//            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

//        httpSecurity.oauth2Login { oauth -> oauth.loginPage("/login").successHandler(null) }

//        http.sessionManagement { session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

//        httpSecurity.authenticationProvider(authenticationProvider)
//        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
