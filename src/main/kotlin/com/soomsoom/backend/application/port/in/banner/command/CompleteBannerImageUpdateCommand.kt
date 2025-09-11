package com.soomsoom.backend.application.port.`in`.banner.command

data class CompleteBannerImageUpdateCommand(
    val bannerId: Long,
    val imageFileKey: String,
)
