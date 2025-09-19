package com.soomsoom.backend.adapter.`in`.web.api.achievement.request

import com.soomsoom.backend.application.port.`in`.achievement.command.CreateAchievementCommand
import com.soomsoom.backend.domain.achievement.model.enums.AchievementCategory
import com.soomsoom.backend.domain.achievement.model.enums.AchievementGrade
import com.soomsoom.backend.domain.achievement.model.vo.AchievementReward
import com.soomsoom.backend.domain.achievement.model.vo.DisplayInfo
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

@Schema(description = "업적 생성 요청 DTO")
data class CreateAchievementRequest(
    @field:Schema(description = "업적 이름", example = "성장의 3일")
    @field:NotBlank
    val name: String,

    @field:Schema(description = "업적 달성 시 보여주는 격려 문구", example = "꾸준함이 마음을 단단하게 만들어요!")
    val phrase: String?,

    @field:Schema(description = "업적 등급", example = "BRONZE")
    @field:NotNull
    val grade: AchievementGrade,

    @field:Schema(description = "업적 카테고리", example = "DIARY")
    @field:NotNull
    val category: AchievementCategory,

    @field:Schema(description = "업적 달성 시 표시될 메시지 정보")
    @field:NotNull
    @field:Valid
    val unlockedDisplayInfo: DisplayInfoRequest,

    @field:Schema(description = "업적 보상 정보 (보상이 없으면 null 또는 생략)")
    @field:Valid
    val reward: RewardRequest?,

    @field:Schema(description = "업적 달성 조건 목록")
    @field:NotEmpty
    @field:Valid
    val conditions: List<ConditionRequest>,
) {
    @Schema(description = "화면 표시 정보 DTO")
    data class DisplayInfoRequest(
        @field:Schema(description = "알림/팝업의 제목으로 사용할 문자열", example = "새로운 업적 달성!")
        @field:NotBlank
        val titleTemplate: String,

        @field:Schema(description = "알림/팝업의 본문으로 사용할 문자열. '%s'는 업적 이름으로 대체될 수 있습니다.", example = "[%s] 업적을 축하해요!")
        @field:NotBlank
        val bodyTemplate: String,
    )

    @Schema(description = "보상 정보 DTO")
    data class RewardRequest(
        @field:Schema(description = "보상 포인트", example = "100")
        val points: Int?,

        @field:Schema(description = "보상 아이템 ID", example = "1")
        val itemId: Long?,

        @field:Schema(description = "보상 지급 시 표시될 메시지 정보")
        @field:NotNull
        @field:Valid
        val displayInfo: DisplayInfoRequest,
    )

    fun toCommand(): CreateAchievementCommand {
        return CreateAchievementCommand(
            name = this.name,
            phrase = this.phrase,
            grade = this.grade,
            category = this.category,
            unlockedDisplayInfo = DisplayInfo(
                titleTemplate = this.unlockedDisplayInfo.titleTemplate,
                bodyTemplate = this.unlockedDisplayInfo.bodyTemplate
            ),
            reward = this.reward?.let {
                AchievementReward(
                    points = it.points,
                    itemId = it.itemId,
                    displayInfo = DisplayInfo(
                        titleTemplate = it.displayInfo.titleTemplate,
                        bodyTemplate = it.displayInfo.bodyTemplate
                    )
                )
            },
            conditions = this.conditions.map {
                CreateAchievementCommand.ConditionCommand(
                    type = it.type,
                    targetValue = it.targetValue
                )
            }
        )
    }
}
