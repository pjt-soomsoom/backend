package com.soomsoom.backend.application.port.`in`.user.command

data class UpdateEquippedItemsCommand(
    val userId: Long,
    val hatId: Long?,
    val eyewearId: Long?,
    val backgroundId: Long?,
    val frameId: Long?,
    val floorId: Long?,
    val shelfId: Long?,
)
