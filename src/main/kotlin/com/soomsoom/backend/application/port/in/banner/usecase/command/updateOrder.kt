package com.soomsoom.backend.application.port.`in`.banner.usecase.command

import com.soomsoom.backend.application.port.`in`.banner.command.UpdateBannerOrderCommand
import com.soomsoom.backend.application.port.`in`.banner.dto.BannerAdminResult

interface updateOrder {
    fun updateOrder(command: UpdateBannerOrderCommand): List<BannerAdminResult>
}
