package com.example.gateway.util.jwt

import com.example.gateway.exception.UnauthorizedException
import com.example.gateway.model.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.lang.NonNull
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.UUID
import java.util.function.Function
import javax.crypto.SecretKey

/**
 * Абстрактный класс для утилиты работы с токенами.
 */
abstract class AbstractTokenUtil : TokenUtil {
    /**
     * Получение срока жизни токена.
     *
     * @return Время жизни токена
     */
    abstract fun getExpiration(): Duration

    /**
     * Получение секретного ключа для подписи токена.
     *
     * @return Строка с секретным ключом
     */
    protected abstract fun getSecret(): String

    /**
     * Получение пользовательских данных (claims) для включения в токен.
     *
     * @param user Пользователь
     * @return Map с данными пользователя
     */
    protected abstract fun getClaims(user: User): Map<String, Any>

    override fun generateToken(user: User, id: UUID): String {
        return generateToken(getClaims(user), user.username, id)
    }

    /**
     * Генерация токена на основе пользовательских данных и имени пользователя.
     *
     * @param claims   Пользовательские данные для включения в токен
     * @param username Имя пользователя
     * @return Строка со сгенерированным токеном
     */
    private fun generateToken(claims: Map<String, Any>, username: String, id: UUID): String {
        val now = Instant.now()
        val expirationTime = now.plus(getExpiration().toMillis(), ChronoUnit.MILLIS)

        return Jwts.builder()
            .id(id.toString())
            .claims(claims)
            .subject(username)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expirationTime))
            .signWith(getSignInKey())
            .compact()
    }

    /**
     * Извлечение идентификатора токена.
     *
     * @param token Токен.
     * @return Идентификатора токена.
     */
    fun extractTokenId(token: String?): UUID {
        val id = extractClaim(token!!) { obj: Claims -> obj.id }
        if (id == null) {
            throw UnauthorizedException()
        }

        return UUID.fromString(id)
    }

    /**
     * Извлечение имени пользователя из токена.
     *
     * @param token Токен
     * @return Строка с именем пользователя
     */
    fun extractUsername(token: String): String {
        return extractClaim(token) { obj: Claims -> obj.subject }
    }

    /**
     * Проверка валидности токена.
     *
     * @param token Токен
     * @return true, если токен валиден, иначе false
     */
    fun isValidToken(token: String): Boolean {
        return extractExpiration(token).after(Date())
    }

    /**
     * Извлечение даты истечения срока действия токена.
     *
     * @param token Токен
     * @return Объект Date с датой истечения срока действия токена
     */
    fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims -> obj.expiration }
    }

    private fun <T> extractClaim(token: String, @NonNull claimsResolver: Function<Claims, T>): T {
        try {
            return claimsResolver.apply(extractAllClaims(token))
        } catch (ex: JwtException) {
            throw UnauthorizedException()
        }
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    /**
     * Получение секретного ключа для подписи токена в виде объекта SecretKey.
     *
     * @return Секретный ключ
     */
    private fun getSignInKey(): SecretKey {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(getSecret()))
    }
}
