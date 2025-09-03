package com.soomsoom.backend.domain.user.model

enum class Role {
    ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN
}

class User private constructor(
    val id: Long?,
    val account: Account,
    val role: Role,
    var points: Int,
) {

    fun addPoints(amount: Int) {
        require(amount > 0) { "포인트는 0보다 커야 합니다." }
        this.points += amount
    }

    fun deductPoints(amount: Int) {
        require(amount > 0) { "차감할 포인트는 0보다 커야 합니다." }
        check(this.points >= amount) { "보유한 포인트가 부족합니다." }
        this.points -= amount
    }

    companion object {

        fun from(
            id: Long,
            account: Account,
            role: Role,
            points: Int = 0,
        ): User {
            return User(id, account, role, points)
        }

        fun createAnonymous(deviceId: String): User {
            return User(
                id = null,
                account = Account.Anonymous(deviceId),
                role = Role.ROLE_ANONYMOUS,
                points = 0,
            )
        }

        fun createSocial(socialProvider: String, socialId: String, deviceId: String): User {
            return User(
                id = null,
                account = Account.Social(socialProvider, socialId, deviceId),
                role = Role.ROLE_USER,
                points = 0,
            )
        }

        fun createAdmin(username: String, password: String): User {
            return User(
                id = null,
                account = Account.IdPassword(username, password),
                role = Role.ROLE_ADMIN,
                points = 0,
            )
        }
    }
}
