package com.example.gateway.config.security

import com.example.gateway.config.filter.JWTReactiveAuthorizationFilter
import com.example.gateway.config.security.handler.AuthorizationExceptionHandler
import com.example.gateway.controller.SecurityController
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.server.ServerWebExchange

@Configuration
@EnableWebFluxSecurity
class SecurityConfig : WebFluxConfigurer {
    companion object {
        const val BEARER: String = "Bearer "
        private val EXCLUDED_PATHS: Array<String> = arrayOf("/api/v1/user/register", "/api/v1/user/refresh")
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
    }

    @Bean
    fun filterChain(
        http: ServerHttpSecurity,
        exceptionHandler: AuthorizationExceptionHandler,
        jwtAuthenticationFilter: AuthenticationWebFilter,
        jwtReactiveAuthorizationFilter: JWTReactiveAuthorizationFilter
    ): SecurityWebFilterChain {
        http
            .csrf { it.disable() } // todo: включить потом цсрф
            .cors(Customizer.withDefaults()) // todo: настроить потом
            .httpBasic { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
            .formLogin { it.disable() }
            .authorizeExchange {
                it.pathMatchers(*EXCLUDED_PATHS).permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(jwtReactiveAuthorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling {
                it.authenticationEntryPoint { exchange: ServerWebExchange, ex: AuthenticationException? ->
                    exceptionHandler
                        .handleAuthorizationException(ex?.message ?: "Unauthorized", exchange, HttpStatus.UNAUTHORIZED)
                }

                it.accessDeniedHandler { exchange: ServerWebExchange, ex: AccessDeniedException? ->
                    exceptionHandler
                        .handleAuthorizationException(ex?.message ?: "Forbidden", exchange, HttpStatus.FORBIDDEN)
                }
            }

        return http.build()
    }

    @Bean
    fun authenticationWebFilter(
        jwtConverter: ServerAuthenticationConverter,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
        serverAuthenticationSuccessHandler: ServerAuthenticationSuccessHandler,
        jwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler,
        securityController: SecurityController
    ): AuthenticationWebFilter {
        val authenticationWebFilter = AuthenticationWebFilter(reactiveAuthenticationManager)
        authenticationWebFilter.setRequiresAuthenticationMatcher {
            ServerWebExchangeMatchers.pathMatchers(HttpMethod.POST, "/api/v1/user/login").matches(it)
        }
        authenticationWebFilter.setServerAuthenticationConverter(jwtConverter)
        authenticationWebFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler)
        authenticationWebFilter.setAuthenticationFailureHandler(jwtServerAuthenticationFailureHandler)
        authenticationWebFilter.setSecurityContextRepository(NoOpServerSecurityContextRepository.getInstance())

        return authenticationWebFilter
    }

    @Bean
    fun reactiveAuthenticationManager(
        reactiveUserDetailsService: ReactiveUserDetailsService,
        passwordEncoder: PasswordEncoder
    ): ReactiveAuthenticationManager {
        val manager = UserDetailsRepositoryReactiveAuthenticationManager(reactiveUserDetailsService)
        manager.setPasswordEncoder(passwordEncoder)

        return manager
    }
}
