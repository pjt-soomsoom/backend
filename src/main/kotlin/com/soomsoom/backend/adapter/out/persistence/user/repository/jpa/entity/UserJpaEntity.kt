package com.soomsoom.backend.adapter.out.persistence.user.repository.jpa.entity

import com.soomsoom.backend.domain.user.model.Account
import com.soomsoom.backend.domain.user.model.Role
import com.soomsoom.backend.domain.user.model.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true)
    var deviceId: String?,

    @Enumerated(EnumType.STRING)
    var accountType: AccountType,

    var socialProvider: String?,
    var socialId: String?,

    var username: String?,
    var password: String?,

    @Enumerated(EnumType.STRING)
    val role: Role,
) {
    enum class AccountType {
        ANONYMOUS, SOCIAL, ID_PASSWORD
    }

    // 도메인 객체를 DB 엔티티로 변환
    companion object {
        fun from(user: User): UserJpaEntity {
            return when (val account = user.account) {
                is Account.Anonymous -> UserJpaEntity(
                    user.id ?: 0L,
                    account.deviceId,
                    AccountType.ANONYMOUS,
                    null,
                    null,
                    null,
                    null,
                    user.role
                )
                is Account.Social -> UserJpaEntity(
                    user.id ?: 0L,
                    account.deviceId,
                    AccountType.SOCIAL,
                    account.socialProvider,
                    account.socialId,
                    null,
                    null,
                    user.role
                )
                is Account.IdPassword -> UserJpaEntity(
                    user.id ?: 0L,
                    null,
                    AccountType.ID_PASSWORD,
                    null,
                    null,
                    account.username,
                    account.password,
                    user.role
                )
            }
        }
    }

    // DB 엔티티를 도메인 객체로 변환
    fun toDomain(): User {
        val account = when (this.accountType) {
            AccountType.ANONYMOUS -> Account.Anonymous(this.deviceId!!)
            AccountType.SOCIAL -> Account.Social(this.socialProvider!!, this.socialId!!, this.deviceId!!)
            AccountType.ID_PASSWORD -> Account.IdPassword(this.username!!, this.password!!)
        }
        return User.from(this.id, account, this.role)
    }
}
