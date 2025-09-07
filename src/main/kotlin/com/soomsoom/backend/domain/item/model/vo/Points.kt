package com.soomsoom.backend.domain.item.model.vo

@JvmInline
value class Points(val value: Int) {
    init {
        require(value >= 0) { "포인트는 음수일 수 없습니다." }
    }
}
