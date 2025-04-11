package com.example.gateway.config.security

import com.example.gateway.config.filter.JWTReactiveAuthorizationFilter
import com.example.gateway.config.security.handler.AuthorizationExceptionHandler
import org.springframework.beans.factory.annotation.Value
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

/**
 * Конфигурационный класс для настроек безопасности.
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    @Value("\${security.excluded.get}") private val excludedGetPaths: Array<String>,
    @Value("\${security.excluded.post}") private val excludedPostPaths: Array<String>
) : WebFluxConfigurer {

    companion object {
        const val BEARER: String = "Bearer "
    }

    /**
     * Определяет бин PasswordEncoder для кодирования и проверки паролей.
     *
     * @return Бин Argon2PasswordEncoder.
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
    }

    /**
     * Конфигурирует цепочку фильтров безопасности и механизмы аутентификации.
     *
     * @param http                           Экземпляр ServerHttpSecurity для конфигурации безопасности.
     * @param exceptionHandler               Пользовательский обработчик исключений для авторизационных исключений.
     * @param jwtAuthenticationFilter     Фильтр аутентификации для обработки токенов JWT.
     * @param jwtReactiveAuthorizationFilter Фильтр авторизации для обработки токенов JWT реактивно.
     * @return Настроенная цепочка фильтров безопасности.
     */
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
                it
                    .pathMatchers(HttpMethod.GET, *excludedGetPaths).permitAll()
                    .pathMatchers(HttpMethod.POST, *excludedPostPaths).permitAll()
                    .pathMatchers("/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .addFilterAt(jwtReactiveAuthorizationFilter, SecurityWebFiltersOrder.AUTHORIZATION)
            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
            .exceptionHandling {
                it.authenticationEntryPoint { exchange: ServerWebExchange, ex: AuthenticationException? ->
                    exceptionHandler
                        .handleAuthorizationException(ex?.message ?: "Unauthorized", exchange, HttpStatus.UNAUTHORIZED)
                        .then()
                }

                it.accessDeniedHandler { exchange: ServerWebExchange, ex: AccessDeniedException? ->
                    exceptionHandler
                        .handleAuthorizationException(ex?.message ?: "Forbidden", exchange, HttpStatus.FORBIDDEN)
                        .then()
                }
            }

        return http.build()
    }

    /**
     * Конфигурирует AuthenticationWebFilter для обработки токенов JWT.
     *
     * @param jwtConverter                          ServerAuthenticationConverter для преобразования токенов JWT.
     * @param reactiveAuthenticationManager         ReactiveAuthenticationManager для управления аутентификацией.
     * @param serverAuthenticationSuccessHandler    Обработчик успешной аутентификации.
     * @param jwtServerAuthenticationFailureHandler Обработчик ошибок аутентификации.
     * @return Настроенный AuthenticationWebFilter.
     */
    @Bean
    fun authenticationWebFilter(
        jwtConverter: ServerAuthenticationConverter,
        reactiveAuthenticationManager: ReactiveAuthenticationManager,
        serverAuthenticationSuccessHandler: ServerAuthenticationSuccessHandler,
        jwtServerAuthenticationFailureHandler: ServerAuthenticationFailureHandler
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

    /**
     * Конфигурирует ReactiveAuthenticationManager для управления аутентификацией с использованием реактивного сервиса
     * пользовательских данных.
     *
     * @param reactiveUserDetailsService Реактивный сервис для загрузки пользовательских данных.
     * @param passwordEncoder            PasswordEncoder для кодирования и проверки паролей.
     * @return Настроенный ReactiveAuthenticationManager.
     */
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
