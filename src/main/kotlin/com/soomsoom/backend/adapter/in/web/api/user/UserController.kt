package com.soomsoom.backend.adapter.`in`.web.api.user

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.adapter.`in`.web.api.user.request.AnswerOnboardingRequest
import com.soomsoom.backend.adapter.`in`.web.api.user.request.UpdateEquippedItemsRequest
import com.soomsoom.backend.application.port.`in`.activityhistory.dto.FindMySummaryResult
import com.soomsoom.backend.application.port.`in`.activityhistory.usecase.query.FindMySummaryUseCase
import com.soomsoom.backend.application.port.`in`.favorite.dto.FavoriteActivityResult
import com.soomsoom.backend.application.port.`in`.favorite.usecase.query.FindFavoriteActivitiesUseCase
import com.soomsoom.backend.application.port.`in`.follow.dto.FollowingInstructorResult
import com.soomsoom.backend.application.port.`in`.follow.usecase.query.FindFollowingInstructorsUseCase
import com.soomsoom.backend.application.port.`in`.item.dto.CollectionDto
import com.soomsoom.backend.application.port.`in`.item.dto.ItemDto
import com.soomsoom.backend.application.port.`in`.todaymission.dto.TodayMissionResult
import com.soomsoom.backend.application.port.`in`.todaymission.usecase.query.FindTodayMissionUseCase
import com.soomsoom.backend.application.port.`in`.user.command.UpdateEquippedItemsCommand
import com.soomsoom.backend.application.port.`in`.user.dto.EquippedItemsDto
import com.soomsoom.backend.application.port.`in`.user.dto.UserPoints
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedCollectionsCriteria
import com.soomsoom.backend.application.port.`in`.user.query.FindOwnedItemsCriteria
import com.soomsoom.backend.application.port.`in`.user.usecase.command.AnswerOnboardingUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.DeleteUserUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.command.UpdateEquippedItemsUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindEquippedItemsUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindOwnedCollectionsUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindOwnedItemsUseCase
import com.soomsoom.backend.application.port.`in`.user.usecase.query.FindUserPointsUseCase
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.item.model.enums.ItemType
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users/me")
class UserController(
    private val findFavoriteActivitiesUseCase: FindFavoriteActivitiesUseCase,
    private val findFollowingInstructorsUseCase: FindFollowingInstructorsUseCase,
    private val findMySummaryUseCase: FindMySummaryUseCase,
    private val updateEquippedItemsUseCase: UpdateEquippedItemsUseCase,
    private val findOwnedItemsUseCase: FindOwnedItemsUseCase,
    private val findEquippedItemsUseCase: FindEquippedItemsUseCase,
    private val findOwnedCollectionsUseCase: FindOwnedCollectionsUseCase,
    private val findUserPointsUseCase: FindUserPointsUseCase,
    private val findTodayMissionUseCase: FindTodayMissionUseCase,
    private val answerOnboardingUseCase: AnswerOnboardingUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) {

    /**
     * 내가 즐겨찾기한 활동 목록 조회
     */
    @GetMapping("/favorites")
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
    @GetMapping("/following")
    @ResponseStatus(HttpStatus.OK)
    fun findMyFollowing(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        pageable: Pageable,
        @RequestParam(required = false) userId: Long?,
    ): Page<FollowingInstructorResult> {
        val targetUserId = userId ?: userDetails.id
        return findFollowingInstructorsUseCase.find(targetUserId, pageable)
    }

    /**
     * 마이페이지 활동 요약 정보 조회
     */
    @GetMapping("/summary")
    @ResponseStatus(HttpStatus.OK)
    fun getMySummary(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) userId: Long?,
    ): FindMySummaryResult {
        val targetUserId = userId ?: userDetails.id
        return findMySummaryUseCase.find(targetUserId)
    }

    /**
     * 현재 장착 중인 아이템 목록을 조회
     */
    @GetMapping("/equipped-items")
    fun getMyEquippedItems(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): EquippedItemsDto {
        return findEquippedItemsUseCase.findEquippedItems(userDetails.id)
    }

    /**
     * 현재 장착 아이템 목록을 수정(전체 교체)
     */
    @PutMapping("/equipped-items")
    @ResponseStatus(HttpStatus.OK)
    fun updateMyEquippedItems(
        @Valid @RequestBody
        request: UpdateEquippedItemsRequest,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ) {
        val command = UpdateEquippedItemsCommand(userDetails.id, request.itemsToEquip)
        updateEquippedItemsUseCase.updateEquippedItems(command)
    }

    /**
     * 내가 소유한 아이템 목록을 조회
     */
    @GetMapping("/owned-items")
    fun getMyOwnedItems(
        @RequestParam(required = false) userId: Long?,
        @RequestParam(required = false) itemType: ItemType?,
        pageable: Pageable,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) deletionStatus: DeletionStatus?,
    ): Page<ItemDto> {
        val targetUserId = userId ?: userDetails.id
        val criteria = FindOwnedItemsCriteria(
            userId = targetUserId,
            itemType = itemType,
            pageable = pageable,
            deletionStatus = deletionStatus ?: DeletionStatus.ACTIVE
        )
        return findOwnedItemsUseCase.findOwnedItems(criteria)
    }

    /**
     * 내가 소유한 컬렉션 목록을 조회
     */
    @GetMapping("/owned-collections")
    fun getMyOwnedCollections(
        @RequestParam(required = false) userId: Long?,
        pageable: Pageable,
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): Page<CollectionDto> {
        val targetUserId = userId ?: userDetails.id
        val criteria = FindOwnedCollectionsCriteria(
            userId = targetUserId,
            pageable = pageable
        )
        return findOwnedCollectionsUseCase.findOwnedCollections(criteria)
    }

    /**
     * heartPoints 조회
     */
    @GetMapping("/points")
    @ResponseStatus(HttpStatus.OK)
    fun getMyPoints(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) userId: Long?,
    ): UserPoints {
        val targetUserId = userId ?: userDetails.id
        return findUserPointsUseCase.findUserPoints(targetUserId)
    }

    /**
     * 오늘의 미션 상태 조회(홈 화면용)
     */
    @GetMapping("/today-missions")
    @ResponseStatus(HttpStatus.OK)
    fun getTodayMissions(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
    ): TodayMissionResult {
        return findTodayMissionUseCase.find(userDetails.id)
    }

    /**
     * 온보딩 질문 답변 저장
     */
    @PostMapping("/onboarding-answers")
    @ResponseStatus(HttpStatus.OK)
    fun answerOnboarding(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @Valid @RequestBody
        request: AnswerOnboardingRequest,
    ) {
        answerOnboardingUseCase.answer(request.toCommand(userDetails.id))
    }

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴", description = "유저의 회원 탈퇴 기능입니다. userId를 파라미터로 넘기지 않는 경우 로그인한 유저 본인을 탈퇴 처리합니다.")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun delete(
        @AuthenticationPrincipal userDetails: CustomUserDetails,
        @RequestParam(required = false) userId: Long?
    ) {
        val deleteUserId = userId ?: userDetails.id
        deleteUserUseCase.delete(deleteUserId)
    }
}
