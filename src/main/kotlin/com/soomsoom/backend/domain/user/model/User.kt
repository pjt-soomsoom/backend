package com.soomsoom.backend.domain.user.model

enum class Role {
    ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN
}

class User private constructor(
    val id: Long?,
    val account: Account,
    val role: Role,
) {

    companion object {

        fun from(
            id: Long,
            account: Account,
            role: Role,
        ): User {
            return User(id, account, role)
        }

        fun createAnonymous(deviceId: String): User {
            return User(
                id = null,
                account = Account.Anonymous(deviceId),
                role = Role.ROLE_ANONYMOUS
            )
        }

        fun createSocial(socialProvider: String, socialId: String, deviceId: String): User {
            return User(
                id = null,
                account = Account.Social(socialProvider, socialId, deviceId),
                role = Role.ROLE_USER
            )
        }

        fun createAdmin(username: String, password: String): User {
            return User(
                id = null,
                account = Account.IdPassword(username, password),
                role = Role.ROLE_ADMIN
            )
        }
    }
}
