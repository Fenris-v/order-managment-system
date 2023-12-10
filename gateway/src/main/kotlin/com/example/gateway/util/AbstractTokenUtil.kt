package com.example.gateway.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.security.core.userdetails.UserDetails
import reactor.core.publisher.Mono
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

abstract class AbstractTokenUtil : TokenUtil {
    protected abstract fun getSignInKey(): Mono<SecretKey>
    protected abstract fun generateToken(claims: Map<String, Any>, username: String?): Mono<String>

    override fun generateToken(userDetails: UserDetails): Mono<String> {
        val claims: Map<String, Any> = HashMap()
        return generateToken(claims, userDetails.username)
    }

    fun extractUsername(token: String): Mono<String> {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Mono<Date> {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): Mono<T> {
        return extractAllClaims(token).map { claimsResolver.apply(it) }
    }

    fun extractAllClaims(token: String): Mono<Claims> {
        return getSignInKey().map {
            Jwts
                .parser()
                .verifyWith(it)
                .build()
                .parseSignedClaims(token)
                .payload
        }
    }

    fun isValidToken(token: String): Mono<Boolean> {
        return extractExpiration(token).map { it.after(Date()) }
    }
}
