package com.soomsoom.backend.application.port.out.useractivity

import com.soomsoom.backend.domain.useractivity.model.aggregate.ScreenTimeLog

interface ScreenTimeLogPort {
    fun save(screenTimeLog: ScreenTimeLog): ScreenTimeLog
    fun deleteByUserId(userId: Long)
}
