package com.soomsoom.backend.application.port.`in`.auth.usecase

import com.soomsoom.backend.application.port.`in`.auth.TokenInfo
import com.soomsoom.backend.application.port.`in`.auth.command.AdminSignUpCommand

interface AdminSignUpUseCase {

    /**
 * 관리자가 회원가입을 수행하고 인증 토큰 정보를 반환합니다.
 *
 * @param command 관리자 회원가입에 필요한 정보를 담은 명령 객체입니다.
 * @return 생성된 인증 토큰 정보입니다.
 */
fun adminSignUp(command: AdminSignUpCommand): TokenInfo
}
