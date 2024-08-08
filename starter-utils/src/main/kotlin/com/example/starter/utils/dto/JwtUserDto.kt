package com.example.starter.utils.dto

/**
 * Класс данных (DTO) для представления пользователя в JWT (JSON Web Token).
 * <p>
 * Этот класс данных содержит поля, которые представляют в полезной нагрузке, которая будет включена в JSON Web Token
 * (JWT).
 */
data class JwtUserDto(val id: Long, val email: String)
