package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerOrderCommand
import jakarta.validation.constraints.NotEmpty

data class UpdateBannerOrderRequest(
    @field:NotEmpty(message = "배너 ID 목록은 비워둘 수 없습니다.")
    val orderedBannerIds: List<Long>?,
) {
    fun toCommand(): UpdateBannerOrderCommand {
        return UpdateBannerOrderCommand(
            orderedBannerIds = this.orderedBannerIds!!
        )
    }
}
