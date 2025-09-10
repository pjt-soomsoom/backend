package com.soomsoom.backend.domain.common.vo

import com.soomsoom.backend.common.exception.DomainErrorReason

@JvmInline
value class Points(val value: Int) {
    init {
        require(value >= 0) { "포인트는 음수일 수 없습니다." }
    }

    operator fun plus(amount: Points): Points {
        return Points(this.value + amount.value)
    }

    // '-' 연산자 오버로딩
    operator fun minus(amount: Points): Points {
        // 비즈니스 규칙: 차감할 포인트가 현재 포인트보다 많을 수 없다.
        check(this.value >= amount.value) { DomainErrorReason.NOT_ENOUGH_POINTS }
        return Points(this.value - amount.value)
    }
}
