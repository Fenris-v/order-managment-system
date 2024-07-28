package com.example.gateway.dto

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Класс представляет сущность сгенерированного случайного пароля.
 */
data class NinjaPasswordResponse(@JsonAlias("random_password") @JsonProperty("password") val password: String)
