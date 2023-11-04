package com.example.gateway.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

@Component
// todo
class JwtUtil(@Value("\${app.auth.secret}") private val secret: String) {
    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseUnsecuredClaims(token)
            .payload
    }

    fun generateJWT(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return generateJWT(claims, userDetails.username)
    }

    private fun generateJWT(claims: Map<String, Any>, username: String?): String {
        return Jwts.builder()
            .claims(claims)
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
            .signWith(getSignInKey())
            .compact()
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes: ByteArray = Decoders.BASE64.decode(secret)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username: String = extractUsername(token)
        return (username == userDetails.username) && !isTokenExpired(token)
    }

    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }
}
