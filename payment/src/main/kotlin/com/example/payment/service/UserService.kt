package com.example.payment.service

import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

// TODO
@Service
class UserService {
    fun getUser(): Mono<User> = Mono.just(User())// todo
}

data class User(
    val id: Long = 1, val name: String = "name", val lastname: String = "lastname", val email: String = "email@mail.ru"
)
