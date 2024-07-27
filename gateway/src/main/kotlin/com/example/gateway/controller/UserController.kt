package com.example.gateway.controller

import com.example.gateway.dto.request.security.EmailChangingRequest
import com.example.gateway.dto.request.security.UserUpdatingRequest
import com.example.gateway.dto.response.EmptyResponse
import com.example.gateway.dto.response.FullUserDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.dto.response.UserDto
import com.example.gateway.service.UserDetailsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@Tag(name = "Пользователь")
@RequestMapping("/api/v1/user")
class UserController(private val userService: UserDetailsService) {
    /**
     * Получить текущего пользователя.
     *
     * @return Моно с FullUserDto.
     */
    @GetMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Данные пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FullUserDto::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "Получить пользователя", description = "Получить текущего пользователя.")
    fun getUser(): Mono<FullUserDto> {
        return userService.getCurrentUserResponse()
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param userId Идентификатор пользователя.
     * @return Моно с UserDto.
     */
    @GetMapping("/{userId}")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Данные пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = UserDto::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация"),
            ApiResponse(responseCode = "404", description = "Пользователь не найден")
        ]
    )
    @Operation(summary = "Получить пользователя", description = "Получить пользователя по идентификатору.")
    fun getUser(@PathVariable userId: Long): Mono<UserDto> {
        return userService.getUserById(userId)
    }

    /**
     * Изменить текущего пользователя.
     *
     * @param request Объект UserUpdatingRequest, содержащий новые данные пользователя.
     * @return Моно с FullUserDto.
     */
    @PutMapping
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Данные пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = FullUserDto::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "Изменить пользователя", description = "Изменить текущего пользователя.")
    fun changeUser(@RequestBody request: UserUpdatingRequest): Mono<FullUserDto> {
        return userService.updateUser(request)
    }

    /**
     * Запросить письмо для смены email.
     *
     * @param request Объект EmailChangingRequest, содержащий пароль и новый email.
     * @return Моно с EmptyResponse.
     */
    @PutMapping("/email")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Данные пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmptyResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "Изменить email пользователя", description = "Изменить email текущего пользователя.")
    fun changeEmail(@RequestBody request: EmailChangingRequest): Mono<ResponseEntity<EmptyResponse>> {
        return userService.changeEmail(request)
            .then(Mono.just(ResponseEntity.accepted().body(EmptyResponse("Письмо для смены email отправлено"))))
    }

    /**
     * Запросить письмо для смены email.
     *
     * @param token Токен для верификации.
     * @return Моно с JwtResponse.
     */
    @GetMapping("/email")
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Данные пользователя",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = EmptyResponse::class)
                )]
            ),
            ApiResponse(responseCode = "401", description = "Требуется авторизация")
        ]
    )
    @Operation(summary = "Изменить email пользователя", description = "Изменить email текущего пользователя.")
    fun changeEmail(@RequestParam token: String): Mono<JwtResponse> {
        return userService.verifyChangeEmail(token)
    }
}
