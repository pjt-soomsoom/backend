package com.soomsoom.backend.adapter.`in`.web.api.adrewardlog

import com.soomsoom.backend.application.port.`in`.adrewardlog.command.VerifyAdRewardCommand
import com.soomsoom.backend.application.port.`in`.adrewardlog.usecase.command.VerifyAdRewardUseCase
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "외부 콜백", description = "외부 서비스로부터 받는 콜백 API")
@RestController
@RequestMapping("/callbacks/admob")
class AdRewardCallbackController(
    private val verifyAdRewardUseCase: VerifyAdRewardUseCase,
) {
    @Operation(
        summary = "Google AdMob SSV 콜백 수신",
        description = "Google AdMob 서버로부터 보상형 광고 시청 완료에 대한 서버 측 검증(SSV) 콜백을 수신합니다. **클라이언트에서 직접 호출하는 API가 아닙니다.**"
    )
    @GetMapping("/ssv")
    @ResponseStatus(HttpStatus.OK)
    fun handleSsvCallback(
        @Parameter(description = "사용자 ID")
        @RequestParam("user_id")
        userId: String,
        @Parameter(description = "광고 단위 ID")
        @RequestParam("ad_unit")
        adUnitId: String,
        @Parameter(description = "고유 트랜잭션 ID")
        @RequestParam("transaction_id")
        transactionId: String,
        @Parameter(description = "보상 아이템 이름")
        @RequestParam("reward_item")
        rewardItem: String,
        @Parameter(description = "보상량")
        @RequestParam("reward_amount")
        rewardAmount: String,
        @Parameter(description = "타임스탬프")
        @RequestParam("timestamp")
        timestamp: String,
        request: HttpServletRequest,
    ) {
        val command = VerifyAdRewardCommand(
            userId = userId,
            adUnitId = adUnitId,
            transactionId = transactionId,
            rewardAmount = rewardAmount,
            rewardItem = rewardItem,
            timestamp = timestamp,
            fullCallbackUrl = "${request.requestURL}?${request.queryString}"
        )
        verifyAdRewardUseCase.command(command)
    }
}
