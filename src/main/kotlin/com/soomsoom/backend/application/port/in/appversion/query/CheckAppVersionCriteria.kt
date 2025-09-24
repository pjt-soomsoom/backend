package com.soomsoom.backend.application.port.`in`.appversion.query

import com.soomsoom.backend.common.entity.enums.OSType

data class CheckAppVersionCriteria(
    val os: OSType,
    val versionName: String,
)
