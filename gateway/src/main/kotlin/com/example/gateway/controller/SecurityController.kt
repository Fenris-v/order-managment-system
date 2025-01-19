package com.example.gateway.controller

import com.example.gateway.dto.NinjaPasswordResponse
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.request.security.EmailRequest
import com.example.gateway.dto.request.security.PasswordChangingRequest
import com.example.gateway.dto.request.security.RefreshDto
import com.example.gateway.dto.response.EmptyResponse
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.service.UserDetailsService
import com.example.starter.utils.dto.response.ExceptionDto
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * Контроллер для обработки запросов, связанных с безопасностью и аутентификацией.
 */
@RestController
@Tag(name = "Безопасность")
@RequestMapping("/api/v1/user")
class SecurityController(private val userService: UserDetailsService) {

    /**
     * Выполняет регистрацию нового пользователя.
     *
     * @param authDto Регистрационные данные.
     * @return ResponseEntity с пустым телом и кодом ответа 200, если успешно.
     */
    @PostMapping("/register")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Пользователь зарегистрирован",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = JwtResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422",
                description = "Ошибка валидации",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = ExceptionDto::class)
                )]
            )
        ]
    )
    @Operation(summary = "Регистрация", description = "Регистрация нового пользователя.")
    fun register(@RequestBody @Valid authDto: AuthDto): Mono<ResponseEntity<JwtResponse>> {
        return userService.createUser(authDto).map { ResponseEntity.status(HttpStatus.CREATED).body(it) }
    }

    /**
     * Обновляет токены доступа и обновления при запросе на обновление токена.
     *
     * @param refreshDto Объект RefreshDto, содержащий токен обновления.
     * @return Mono с объектом JwtResponse.
     */
    @PostMapping("/refresh")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Новые токены доступа и обновления",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = JwtResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Токен обновления недействителен")
        ]
    )
    @Operation(summary = "Получение новых токенов", description = "Получение новых токенов доступа и обновления.")
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
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Выход выполнен",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmptyResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
        ]
    )
    @Operation(summary = "Выход", description = "Выход пользователя из системы.")
    fun logout(@RequestHeader authorization: String): Mono<ResponseEntity<EmptyResponse>> {
        return userService.logout(authorization)
            .thenReturn(ResponseEntity.ok().body(EmptyResponse("Выход выполнен")))
    }

    // Фиктивный метод для документирования в Swagger.
    @PostMapping("/login")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Успешный вход",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = JwtResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "422", description = "Пользователь не найден", content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = ExceptionDto::class))
                ]
            )
        ]
    )
    @Operation(summary = "Логин", description = "Выполняет вход пользователя в систему.")
    private fun login(@RequestBody @Valid authDto: AuthDto): Mono<ResponseEntity<JwtResponse>> {
        throw RuntimeException()
    }

    /**
     * Изменить пароль пользователя.
     *
     * @param authorization Заголовок авторизации с токеном доступа.
     * @param request Объект PasswordChangingRequest, содержащий старый и новый пароль.
     * @return ResponseEntity с пустым телом и кодом ответа 200, если успешно.
     */
    @PostMapping("/password")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пароль изменен",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = EmptyResponse::class))
                ]
            ),
            ApiResponse(
                responseCode = "422", description = "Пароли не совпадают", content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = ExceptionDto::class))
                ]
            )
        ]
    )
    @Operation(summary = "Изменить пароль", description = "Изменить пароль пользователя.")
    fun changePassword(
        @RequestHeader authorization: String,
        @RequestBody @Valid request: PasswordChangingRequest
    ): Mono<ResponseEntity<EmptyResponse>> {
        return userService.changePassword(authorization, request)
            .then(Mono.just(ResponseEntity.ok().body(EmptyResponse("Пароль изменён"))))
    }

    /**
     * Сброс пароля пользователя.
     *
     * @return ResponseEntity с пустым телом и кодом ответа 200, если успешно.
     */
    @PostMapping("/password/reset")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пароль изменен и отправлен на почту",
                content = [
                    Content(mediaType = "application/json", schema = Schema(implementation = EmptyResponse::class))
                ]
            )
        ]
    )
    @Operation(summary = "Изменить пароль", description = "Изменить пароль пользователя.")
    fun resetPassword(@RequestBody @Valid request: EmailRequest): Mono<ResponseEntity<EmptyResponse>> {
        return userService.resetPassword(request)
            .then(Mono.just(ResponseEntity.ok().body(EmptyResponse("Новый пароль отправлен на почту"))))
    }

    /**
     * Сгенерировать пароль.
     *
     * @return Mono с объектом NinjaPasswordResponse.
     */
    @GetMapping("/password")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Пароль сгенерирован",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = NinjaPasswordResponse::class)
                    )
                ]
            )
        ]
    )
    @Operation(summary = "Изменить пароль", description = "Изменить пароль пользователя.")
    fun generatePassword(@RequestParam length: Int? = null): Mono<NinjaPasswordResponse> {
        return userService.generatePassword(length)
    }
}
