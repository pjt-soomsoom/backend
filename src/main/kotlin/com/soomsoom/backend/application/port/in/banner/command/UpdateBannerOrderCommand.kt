package com.soomsoom.backend.application.port.`in`.banner.command

data class UpdateBannerOrderCommand(
    val orderedBannerIds: List<Long>,
)
