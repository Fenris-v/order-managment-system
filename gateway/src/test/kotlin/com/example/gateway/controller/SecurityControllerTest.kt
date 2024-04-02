package com.example.gateway.controller

import com.example.gateway.AbstractApplicationTest
import com.example.gateway.dto.request.security.AuthDto
import com.example.gateway.dto.request.security.RefreshDto
import com.example.gateway.dto.response.JwtResponse
import com.example.gateway.enums.UserRoleType
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult

@ExtendWith(SpringExtension::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class SecurityControllerTest : AbstractApplicationTest() {
    @Autowired
    lateinit var webTestClient: WebTestClient

    companion object {
        private val authDto = AuthDto(TEST_USER_USERNAME, TEST_USER_PASSWORD)
    }

    @Test
    fun register() {
        val username = "resgister@mail.ru"
        val response = register(AuthDto(username, TEST_USER_PASSWORD))

        response.expectStatus().isEqualTo(HttpStatus.CREATED)
            .expectBody()
            .jsonPath("$.email").isEqualTo(username)
            .jsonPath("$.roles").isArray()
            .jsonPath("$.roles.length()").isEqualTo(1)
            .jsonPath("$.roles[0]").isEqualTo(UserRoleType.ROLE_USER.name)
            .jsonPath("$.name").doesNotExist()
            .jsonPath("$.lastname").doesNotExist()
    }

    @Test
    fun registerAndLogin() {
        val username = "resgister-login@mail.ru"
        val authDto = AuthDto(username, TEST_USER_PASSWORD)

        register(authDto)
        val response = login(authDto)

        response.expectStatus().isEqualTo(HttpStatus.OK)
    }

    @Test
    fun login() {
        val authDtoWithWrongPassword = AuthDto(TEST_USER_USERNAME, "wrong$TEST_USER_PASSWORD")
        var response = login(authDtoWithWrongPassword)
        response.expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY)

        response = login(authDto)
        checkAccessTokenResponse(response)
    }

    @Test
    fun refreshToken() {
        val loginResponse = login(authDto)
        val refreshToken: String? = loginResponse.returnResult<JwtResponse>().responseBody.blockFirst()?.refreshToken
        assertNotNull(refreshToken)

        val refreshDto = RefreshDto(refreshToken!!)
        var response = refresh(refreshDto)
        checkAccessTokenResponse(response)

        response = refresh(refreshDto)
        response.expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    @Test
    fun logout() {
        val loginResponse = login(authDto)
        val refreshToken: String? = loginResponse.returnResult<JwtResponse>().responseBody.blockFirst()?.refreshToken
        val accessToken: String? = loginResponse.returnResult<JwtResponse>().responseBody.blockFirst()?.accessToken

        assertNotNull(refreshToken)
        assertNotNull(accessToken)

        var response = logout(accessToken!!)
        response.expectStatus().isEqualTo(HttpStatus.OK)

        response = logout(accessToken)
        response.expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)

        response = refresh(RefreshDto(refreshToken!!))
        response.expectStatus().isEqualTo(HttpStatus.UNAUTHORIZED)
    }

    private fun register(authDto: AuthDto): WebTestClient.ResponseSpec {
        return webTestClient.post()
            .uri("/api/v1/user/register")
            .bodyValue(authDto)
            .exchange()
    }

    private fun login(authDto: AuthDto): WebTestClient.ResponseSpec {
        return webTestClient.post()
            .uri("/api/v1/user/login")
            .bodyValue(authDto)
            .exchange()
    }

    private fun refresh(refreshDto: RefreshDto): WebTestClient.ResponseSpec {
        return webTestClient.post()
            .uri("/api/v1/user/refresh")
            .bodyValue(refreshDto)
            .exchange()
    }

    private fun logout(accessToken: String): WebTestClient.ResponseSpec {
        return webTestClient.post()
            .uri("/api/v1/user/logout")
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .exchange()
    }

    private fun checkAccessTokenResponse(response: WebTestClient.ResponseSpec) {
        response.expectStatus().isEqualTo(HttpStatus.OK)
            .expectBody()
            .jsonPath("$.accessToken").exists()
            .jsonPath("$.refreshToken").exists()
            .jsonPath("$.expiresIn").isNumber()
            .jsonPath("$.tokenType").isEqualTo("Bearer")
            .jsonPath("$.timestamp").isNotEmpty
    }
}
