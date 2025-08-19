package com.soomsoom.backend.application.port.`in`.activity.usecase.command

interface SoftDeleteActivityUseCase {
    fun softDeleteActivity(activityId: Long)
}
