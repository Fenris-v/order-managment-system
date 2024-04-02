package com.example.gateway.config.filter

import com.example.gateway.config.security.handler.AuthorizationExceptionHandler
import com.example.gateway.dto.response.ExceptionDto
import com.example.gateway.repository.AccessTokenRepository
import com.example.gateway.service.UserDetailsService
import com.example.gateway.util.JwtUtil
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpHeaders
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class JWTReactiveAuthorizationFilterTest {
    @InjectMocks
    private lateinit var jwtReactiveAuthorizationFilter: JWTReactiveAuthorizationFilter

    @Mock
    private lateinit var jwtUtil: JwtUtil

    @Mock
    private lateinit var objectMapper: ObjectMapper

    @Mock
    private lateinit var userDetailsService: UserDetailsService

    @Mock
    private lateinit var accessTokenRepository: AccessTokenRepository

    @Mock
    private lateinit var exceptionHandler: AuthorizationExceptionHandler

    @Test
    fun filter() {
        val filterChain = WebFilterChain { filterExchange: ServerWebExchange? -> Mono.empty() }
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "value")
        )

        StepVerifier.create(jwtReactiveAuthorizationFilter.filter(exchange, filterChain))
            .expectSubscription()
            .verifyComplete()
    }

    @Test
    fun filterNotValidToken() {
        val filterChain = WebFilterChain { filterExchange: ServerWebExchange? -> Mono.error(Exception("Error")) }
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer value")
        )

        val testUUID = UUID.randomUUID()

        `when`(objectMapper.writeValueAsBytes(any(ExceptionDto::class.java))).thenReturn(byteArrayOf())
        `when`(jwtUtil.extractTokenId(any())).thenReturn(testUUID)
        `when`(accessTokenRepository.existsById(testUUID)).thenReturn(Mono.just(true))

        val result = jwtReactiveAuthorizationFilter.filter(exchange, filterChain)

        StepVerifier.create(result)
            .expectComplete()
            .verify()
    }

    @Test
    fun filterWithError() {
        val filterChain = WebFilterChain { filterExchange: ServerWebExchange? -> Mono.error(Exception("Error")) }
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer value")
        )

        val testUUID = UUID.randomUUID()

        `when`(jwtUtil.isValidToken(any(String::class.java))).thenReturn(true)
        `when`(jwtUtil.extractTokenId(any(String::class.java))).thenReturn(testUUID)
        `when`(accessTokenRepository.existsById(testUUID)).thenReturn(Mono.just(true))

        val result = jwtReactiveAuthorizationFilter.filter(exchange, filterChain)

        StepVerifier.create(result)
            .expectError()
            .verify()
    }

    @Test
    fun notValidTokenWithError() {
        val filterChain = WebFilterChain { filterExchange: ServerWebExchange? -> Mono.error(Exception("Error")) }
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer value")
        )

        val testUUID = UUID.randomUUID()

        `when`(jwtUtil.extractTokenId(any(String::class.java))).thenReturn(testUUID)
        `when`(accessTokenRepository.existsById(testUUID)).thenReturn(Mono.just(true))

        val result = jwtReactiveAuthorizationFilter.filter(exchange, filterChain)

        StepVerifier.create(result)
            .expectError()
            .verify()
    }

    @Test
    fun validTokenWithError() {
        val filterChain = WebFilterChain { filterExchange: ServerWebExchange? -> Mono.error(Exception("Error")) }
        val exchange = MockServerWebExchange.from(
            MockServerHttpRequest.get("/")
                .header(HttpHeaders.AUTHORIZATION, "Bearer value")
        )

        val testUUID = UUID.randomUUID()
        val user = Mockito.mock(UserDetails::class.java)

        `when`(jwtUtil.extractUsername(any(String::class.java))).thenReturn("user")
        `when`(userDetailsService.findByUsername(any(String::class.java))).thenReturn(Mono.just(user))
        `when`(jwtUtil.isValidToken(any(String::class.java))).thenReturn(true)
        `when`(jwtUtil.extractTokenId(any(String::class.java))).thenReturn(testUUID)
        `when`(accessTokenRepository.existsById(testUUID)).thenReturn(Mono.just(true))

        val result = jwtReactiveAuthorizationFilter.filter(exchange, filterChain)

        StepVerifier.create(result)
            .expectError()
            .verify()
    }


    private fun <T> any(type: Class<T>): T = Mockito.any(type)
}
