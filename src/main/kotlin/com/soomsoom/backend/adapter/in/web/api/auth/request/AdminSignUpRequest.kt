package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand

data class AdminSignUpRequest(
    val username: String,
    val password: String,
)

/**
 * AdminSignUpRequest를 AdminSignUpCommand로 변환한다.
 *
 * @return 현재 요청의 username과 password를 포함하는 AdminSignUpCommand 인스턴스
 */
fun AdminSignUpRequest.toCommand() = AdminSignUpCommand(username, password)
