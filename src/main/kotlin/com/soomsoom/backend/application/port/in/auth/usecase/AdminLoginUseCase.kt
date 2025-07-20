package com.soomsoom.backend.application.port.`in`.auth.usecase

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminLoginCommand

interface AdminLoginUseCase {
    /**
 * 관리자 로그인을 처리하고 인증 토큰 정보를 반환합니다.
 *
 * @param command 관리자 로그인에 필요한 정보를 담은 명령 객체입니다.
 * @return 인증에 성공한 관리자의 토큰 정보입니다.
 */
fun adminLogin(command: AdminLoginCommand): TokenInfo
}
