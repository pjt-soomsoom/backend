package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa

import com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity.UserJpaEntity
import com.soomsoom.backend.domain.user.model.enums.SocialProvider
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface UserJpaRepository : JpaRepository<UserJpaEntity, Long> {
    fun findByDeviceId(deviceId: String): UserJpaEntity?
    fun findByUsername(username: String): UserJpaEntity?
    fun findBySocialProviderAndSocialId(provider: SocialProvider, socialId: String): UserJpaEntity?

    @Modifying
    @Query(
        value = """
        INSERT INTO user_owned_items (user_id, item_id)
        SELECT u.id, :itemId
        FROM users u
        WHERE u.id NOT IN (SELECT uoi.user_id FROM user_owned_items uoi WHERE uoi.item_id = :itemId)
    """,
        nativeQuery = true
    )
    fun grantItemToAllUsers(@Param("itemId") itemId: Long): Int
}
