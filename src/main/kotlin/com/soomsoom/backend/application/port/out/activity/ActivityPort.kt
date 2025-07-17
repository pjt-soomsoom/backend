package com.soomsoom.backend.application.port.out.activity

import com.soomsoom.backend.domain.activity.model.Activity

interface ActivityPort {
    fun findById(id: Long): Activity?
}
