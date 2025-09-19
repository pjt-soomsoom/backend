package com.soomsoom.backend.application.port.`in`.adrewardlog.command

data class VerifyAdRewardCommand(
    val userId: String,
    val adUnitId: String,
    val transactionId: String,
    val rewardAmount: String,
    val rewardItem: String,
    val timestamp: String,
    val fullCallbackUrl: String,
)
