package com.soomsoom.backend.adapter.`in`.web.api.useractivity

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.useractivity.request.AccumulateScreenTimeRequest
import com.soomsoom.backend.application.port.`in`.useractivity.command.AccumulateScreenTimeCommand
import com.soomsoom.backend.application.port.`in`.useractivity.usecase.command.AccumulateScreenTimeUseCase
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/user-activities")
class UserActivityController(
    private val accumulateScreenTimeUseCase: AccumulateScreenTimeUseCase,
) {
    @PostMapping("/screen-time")
    @ResponseStatus(HttpStatus.OK)
    fun accumulateScreenTime(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestBody request: AccumulateScreenTimeRequest,
    ) {
        val command = AccumulateScreenTimeCommand(
            userId = userDetails.id,
            durationInSeconds = request.durationInSeconds
        )
        accumulateScreenTimeUseCase.accumulate(command)
    }
}
