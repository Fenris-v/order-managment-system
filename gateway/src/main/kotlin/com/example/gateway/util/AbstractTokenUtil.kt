package com.example.gateway.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import org.springframework.security.core.userdetails.UserDetails
import java.util.Date
import java.util.function.Function
import javax.crypto.SecretKey

abstract class AbstractTokenUtil : TokenUtil {
    protected abstract fun getSignInKey(): SecretKey
    protected abstract fun generateToken(claims: Map<String, Any>, username: String?): String

    override fun generateToken(userDetails: UserDetails): String {
        val claims: Map<String, Any> = HashMap()
        return generateToken(claims, userDetails.username)
    }

    fun extractUsername(token: String): String {
        return extractClaim(token, Claims::getSubject)
    }

    fun extractExpiration(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun <T> extractClaim(token: String, claimsResolver: Function<Claims, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsResolver.apply(claims)
    }

    fun extractAllClaims(token: String): Claims {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    fun isValidToken(token: String): Boolean {
        return extractExpiration(token).after(Date())
    }
}
