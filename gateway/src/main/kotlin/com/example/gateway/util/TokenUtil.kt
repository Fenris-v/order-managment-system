package com.example.gateway.util

import org.springframework.security.core.userdetails.UserDetails

interface TokenUtil {
    fun generateToken(userDetails: UserDetails): String
}
