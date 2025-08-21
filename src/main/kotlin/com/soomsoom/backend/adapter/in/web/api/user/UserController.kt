package com.soomsoom.backend.adapter.`in`.web.api.user

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.`in`.favorite.dto.FavoriteActivityResult
import com.soomsoom.backend.application.port.`in`.favorite.usecase.query.FindFavoriteActivitiesUseCase
import com.soomsoom.backend.application.port.`in`.follow.dto.FollowingInstructorResult
import com.soomsoom.backend.application.port.`in`.follow.usecase.query.FindFollowingInstructorsUseCase
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val findFavoriteActivitiesUseCase: FindFavoriteActivitiesUseCase,
    private val findFollowingInstructorsUseCase: FindFollowingInstructorsUseCase,
) {

    /**
     * 내가 즐겨찾기한 활동 목록 조회
     */
    @GetMapping("/users/me/favorites")
    @ResponseStatus(HttpStatus.OK)
    fun findMyFavorites(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        pageable: Pageable,
        @RequestParam(required = false) userId: Long?,
    ): Page<FavoriteActivityResult> {
        val targetUserId = userId ?: userDetails.id
        return findFavoriteActivitiesUseCase.find(targetUserId, pageable)
    }

    /**
     * 내가 팔로우한 강사 목록 조회
     */
    @GetMapping("/users/me/following")
    @ResponseStatus(HttpStatus.OK)
    fun findMyFollowing(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        pageable: Pageable,
        @RequestParam(required = false) userId: Long?,
    ): Page<FollowingInstructorResult> {
        val targetUserId = userId ?: userDetails.id
        return findFollowingInstructorsUseCase.find(targetUserId, pageable)
    }
}
