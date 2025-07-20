package com.soomsoom.backend.adapter.`in`.web.api.auth.request

import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand

data class AdminLoginRequest(
    val username: String,
    val password: String,
)

/**
 * AdminLoginRequest 객체를 AdminLoginCommand 객체로 변환합니다.
 *
 * @return 현재 AdminLoginRequest의 사용자명과 비밀번호를 포함하는 AdminLoginCommand 객체
 */
fun AdminLoginRequest.toCommand() = AdminLoginCommand(
    username,
    password
)
