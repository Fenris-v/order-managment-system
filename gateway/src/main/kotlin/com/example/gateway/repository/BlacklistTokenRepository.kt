package com.example.gateway.repository

import com.example.gateway.model.BlacklistToken
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import java.util.UUID

interface BlacklistTokenRepository : ReactiveCrudRepository<BlacklistToken, UUID>
