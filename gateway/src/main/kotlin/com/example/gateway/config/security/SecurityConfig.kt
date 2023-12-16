//package com.example.gateway.config.security
//
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.http.HttpStatus
//import org.springframework.security.access.AccessDeniedException
//import org.springframework.security.config.Customizer
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
//import org.springframework.security.config.web.server.SecurityWebFiltersOrder
//import org.springframework.security.config.web.server.ServerHttpSecurity
//import org.springframework.security.core.AuthenticationException
//import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.web.server.SecurityWebFilterChain
//import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint
//import org.springframework.security.web.server.context.ServerSecurityContextRepository
//import org.springframework.web.server.ServerWebExchange
//import reactor.core.publisher.Mono
//
//@Configuration
//@EnableWebFluxSecurity
//class SecurityConfig {
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
//        return Argon2PasswordEncoder(16, 32, 1, 4096, 3)
//    }
//
//    @Bean
//    fun filterChain(
//        http: ServerHttpSecurity,
//        jwtAuthenticationManager: JwtAuthenticationManager,
//        jwtSecurityContextRepository: ServerSecurityContextRepository,
//        jwtAuthenticationFilter: JwtAuthenticationFilter,
//        jwtAuthenticationFilter2: JwtAuthenticationFilter2
//    ): SecurityWebFilterChain {
//        http
//            .csrf { csrf -> csrf.disable() } // todo: включить потом цсрф
//            .cors(Customizer.withDefaults())
//            .httpBasic { it.authenticationEntryPoint(HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)) }
//            .formLogin { it.disable() }
//            .logout { it.disable() }
//
//
////            .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
////            .addFilterBefore(jwtAuthenticationFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
////            .exceptionHandling {
////                it.authenticationEntryPoint()
////            }
//
////            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//            .authenticationManager(jwtAuthenticationManager)
//            .securityContextRepository(jwtSecurityContextRepository)
//
//
////            .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//
//
//            .exceptionHandling {
//                it.authenticationEntryPoint { exchange: ServerWebExchange, ex: AuthenticationException? ->
//                    Mono.defer {
//                        exchange.response.setStatusCode(HttpStatus.UNAUTHORIZED)
//                        val errorMessage = ex?.message ?: "Unauthorized"
//                        val dataBuffer = exchange.response.bufferFactory().wrap(errorMessage.toByteArray())
//                        exchange.response.writeWith(Mono.just(dataBuffer))
//                    }
//                }
//
//                it.accessDeniedHandler { exchange: ServerWebExchange, ex: AccessDeniedException? ->
//                    Mono.fromRunnable { exchange.response.setStatusCode(HttpStatus.FORBIDDEN) }
//                }
//            }
//            .authorizeExchange {
//                it
//                    .pathMatchers("/api/v1/user/register", "/api/v1/user/login", "/actuator/**")
//                    .permitAll()
//            }
//            .authorizeExchange { it.anyExchange().authenticated() }
//            .addFilterAfter(jwtAuthenticationFilter2, SecurityWebFiltersOrder.LAST)
//
//        return http.build()
//    }
//}
