package com.example.gateway.controller

import com.example.gateway.config.security.SecurityConfig.Companion.BEARER
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.request.security.RefreshDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.service.UserDetailsService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер для обработки запросов, связанных с безопасностью и аутентификацией.
 */
@RestController
@RequestMapping("/api/v1/user")
class SecurityController(private val userService: UserDetailsService) {
    /**
     * Выполняет регистрацию нового пользователя.
     *
     * @param authDto Регистрационные данные.
     * @return ResponseEntity с пустым телом и кодом ответа 200, если успешно.
     */
    @PostMapping("/register")
    fun register(@RequestBody @Valid authDto: AuthDto): Mono<ResponseEntity<UserDto>> {
        return userService.createUser(authDto).map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    /**
     * Обновляет токены доступа и обновления при запросе на обновление токена.
     *
     * @param refreshDto Объект RefreshDto, содержащий токен обновления.
     * @return Mono с объектом JwtResponse.
     */
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody refreshDto: RefreshDto): Mono<JwtResponse> {
        return userService.refresh(refreshDto.refreshToken)
    }

    /**
     * Выполняет выход из системы для текущего пользователя при запросе на выход.
     *
     * @param authorization Заголовок авторизации, содержащий токен доступа.
     * @return ResponseEntity с пустым телом и кодом ответа 200, если успешно.
     */
    @PostMapping("/logout")
    fun logout(@RequestHeader authorization: String?): Mono<ResponseEntity<Any>> {
        if (authorization == null || !authorization.startsWith(BEARER)) {
            return Mono.just(ResponseEntity.ok().build())
        }

        return userService.logout(authorization).thenReturn(ResponseEntity.ok().build())
    }
}
