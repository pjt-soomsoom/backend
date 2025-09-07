package com.soomsoom.backend.domain.user.model.vo

import com.soomsoom.backend.common.DomainErrorReason

@JvmInline
value class UserPoints(val value: Int) {
    init {
        require(value >= 0) { "포인트는 음수일 수 없습니다." }
    }

    operator fun plus(amount: Int): UserPoints {
        require(amount > 0) { "추가할 포인트는 0보다 커야 합니다." }
        return UserPoints(this.value + amount)
    }

    operator fun minus(amount: Int): UserPoints {
        require(amount > 0) { "차감할 포인트는 0보다 커야 합니다." }
        check(this.value >= amount) { DomainErrorReason.NOT_ENOUGH_POINTS }
        return UserPoints(this.value - amount)
    }
}

