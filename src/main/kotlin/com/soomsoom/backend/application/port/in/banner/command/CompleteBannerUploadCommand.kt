package com.soomsoom.backend.application.port.`in`.banner.command

data class CompleteBannerUploadCommand(
    val bannerId: Long,
    val imageFileKey: String,
)
