package com.example.gateway.mapper

import com.example.gateway.dto.response.FullUserDto
import com.example.gateway.dto.response.UserDto
import com.example.gateway.model.User
import org.springframework.stereotype.Component

/**
 * Маппер для работы с сущностью {@link User} и её представлениями.
 */
@Component
class UserMapper {
    /**
     * Преобразование сущности {@link User} в представление {@link UserDto}.
     */
    fun mapEntityToDto(user: User): UserDto {
        return UserDto(user.id!!, user.username, user.name, user.lastname)
    }

    /**
     * Преобразование сущности {@link User} в представление {@link FullUserDto}.
     */
    fun mapEntityToFullUserDto(user: User): FullUserDto {
        return FullUserDto(
            user.id!!,
            user.username,
            user.authorities.stream().map { role -> role.toString() }.toList(),
            user.name,
            user.lastname,
            user.verifiedAt,
            user.createdAt,
            user.updatedAt
        )
    }
}
