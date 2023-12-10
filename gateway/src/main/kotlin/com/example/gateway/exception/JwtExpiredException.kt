package com.example.gateway.exception

import org.springframework.security.core.AuthenticationException

class JwtExpiredException : AuthenticationException("JWT is expired")
