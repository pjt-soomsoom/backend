package com.soomsoom.backend.adapter.`in`.web.api.user.request

data class AddUserPointsRequest(
    val userId: Long,
    val amount: Int,
)
