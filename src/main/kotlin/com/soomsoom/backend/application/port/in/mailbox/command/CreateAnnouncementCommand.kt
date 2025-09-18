package com.soomsoom.backend.application.port.`in`.mailbox.command

data class CreateAnnouncementCommand(
    val title: String,
    val content: String,
)
