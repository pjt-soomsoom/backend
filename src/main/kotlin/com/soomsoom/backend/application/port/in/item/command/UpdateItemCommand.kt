package com.soomsoom.backend.application.port.`in`.item.command

data class UpdateItemCommand(
    val itemId: Long,
    val name: String,
    val description: String,
    val phrase: String?,
    val price: Int,
    val totalQuantity: Int?,
)
