package com.soomsoom.backend.common.event

import org.springframework.core.ResolvableType
import org.springframework.core.ResolvableTypeProvider

/**
 * ResolvableTypeProvider를 구현하여 제네릭 타입 소거 문제를 해결
 */
data class Event<T : Payload>(
    val eventType: EventType,
    val payload: T,
) : ResolvableTypeProvider {
    override fun getResolvableType(): ResolvableType {
        return ResolvableType.forClassWithGenerics(javaClass, ResolvableType.forInstance(payload))
    }
}
