package com.soomsoom.backend.adapter.out.persistence.auth.repository

import com.soomsoom.backend.adapter.out.persistence.auth.repository.jpa.RefreshTokenJpaRepository
import com.soomsoom.backend.adapter.out.persistence.auth.repository.jpa.entity.RefreshTokenJpaEntity
import com.soomsoom.backend.application.port.out.auth.RefreshTokenPort
import com.soomsoom.backend.domain.auth.RefreshToken
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : RefreshTokenPort {
    override fun findByToken(token: String): RefreshToken? = refreshTokenJpaRepository.findById(token).orElse(null)?.toDomain()
    override fun save(refreshToken: RefreshToken): RefreshToken = refreshTokenJpaRepository.save(refreshToken.toEntity()).toDomain()
    override fun delete(refreshToken: RefreshToken) = refreshTokenJpaRepository.delete(refreshToken.toEntity())
    override fun findByUserId(userId: Long): List<RefreshToken> = refreshTokenJpaRepository.findByUserId(userId).map { it.toDomain() }
    override fun deleteAllByUserId(userId: Long) = refreshTokenJpaRepository.deleteAllByUserId(userId)

    private fun RefreshToken.toEntity() = RefreshTokenJpaEntity(this.token, this.userId, this.expiryDate)
    private fun RefreshTokenJpaEntity.toDomain() = RefreshToken(this.token, this.userId, this.expiryDate)
}
