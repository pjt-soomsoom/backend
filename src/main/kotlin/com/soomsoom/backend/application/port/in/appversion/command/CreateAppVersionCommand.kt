package com.soomsoom.backend.application.port.`in`.appversion.command

import com.soomsoom.backend.common.entity.enums.OSType

data class CreateAppVersionCommand(
    val os: OSType,
    val versionName: String,
    val forceUpdate: Boolean,
)
