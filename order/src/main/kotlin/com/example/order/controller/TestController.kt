package com.example.order.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class TestController {
    @GetMapping("/test")
    fun test(): Mono<String> {
        return Mono.just("test")
    }
}
