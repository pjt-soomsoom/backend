package com.soomsoom.backend.application.port.`in`.mailbox.command

data class UpdateAnnouncementCommand(
    val id: Long,
    val title: String,
    val content: String,
)
