package com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command

import com.soomsoom.backend.application.port.`in`.adrewardlog.command.VerifyAdRewardCommand

/**
 * AdMob 서버 측 검증(SSV) 콜백을 처리하고 보상을 지급하는 유스케이스
 */
interface VerifyAdRewardUseCase {
    fun command(command: VerifyAdRewardCommand)
}
