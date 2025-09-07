package com.soomsoom.backend.domain.user.model.entity

import com.soomsoom.backend.domain.item.model.enums.AcquisitionType
import java.time.LocalDateTime

class OwnedItem(
    val id: Long = 0L,
    val userId: Long,
    val itemId: Long,
    val acquiredAt: LocalDateTime = LocalDateTime.now(),
    val acquisitionType: AcquisitionType,
) {
}
