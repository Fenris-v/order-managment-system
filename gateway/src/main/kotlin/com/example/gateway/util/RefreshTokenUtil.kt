package com.example.gateway.util

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.Date
import javax.crypto.SecretKey

@Component
class RefreshTokenUtil(
    @Value("\${app.auth.refresh.secret}") private val secret: String,
    @Value("\${app.auth.refresh.expiration}") private val expiration: Int
) : AbstractTokenUtil(), TokenUtil {
    override fun generateToken(claims: Map<String, Any>, username: String?): Mono<String> {
        return getSignInKey()
            .map {
                Jwts.builder()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(Date())
                    .expiration(Date(System.currentTimeMillis() + expiration))
                    .signWith(it)
                    .compact()
            }
    }

    override fun getSignInKey(): Mono<SecretKey> {
        val keyBytes: ByteArray = Decoders.BASE64.decode(secret)
        return Mono.just(Keys.hmacShaKeyFor(keyBytes))
    }
}
