package com.example.gateway.config.security

import org.springframework.security.core.AuthenticationException

class AuthenticationException2(msg: String?, cause: Throwable?) : AuthenticationException(msg, cause)
