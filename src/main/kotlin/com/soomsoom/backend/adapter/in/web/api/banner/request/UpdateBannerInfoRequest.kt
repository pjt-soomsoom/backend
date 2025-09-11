package com.soomsoom.backend.adapter.`in`.web.api.banner.request

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerInfoCommand
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class UpdateBannerInfoRequest(
    @field:NotBlank(message = "설명은 비워둘 수 없습니다.")
    val description: String?,
    @field:NotBlank(message = "버튼 텍스트는 비워둘 수 없습니다.")
    val buttonText: String?,
    @field:NotNull(message = "연결할 Activity ID는 필수입니다.")
    val linkedActivityId: Long?,
    @field:NotNull(message = "활성화 여부는 필수입니다.")
    val isActive: Boolean?,
    val displayOrder: Int?, // 순서는 선택적으로 변경
) {
    fun toCommand(bannerId: Long): UpdateBannerInfoCommand {
        return UpdateBannerInfoCommand(
            bannerId = bannerId,
            description = this.description!!,
            buttonText = this.buttonText!!,
            linkedActivityId = this.linkedActivityId!!,
            isActive = this.isActive!!,
            displayOrder = this.displayOrder
        )
    }
}
