package com.example.gateway.repository

import com.example.gateway.model.RefreshToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface RefreshTokenRepository : ReactiveCrudRepository<RefreshToken, UUID>
