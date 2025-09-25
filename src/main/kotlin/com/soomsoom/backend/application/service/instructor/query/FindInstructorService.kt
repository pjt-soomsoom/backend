package com.soomsoom.backend.application.service.instructor.query

import com.soomsoom.backend.application.port.`in`.instructor.dto.FindInstructorResult
import com.soomsoom.backend.application.port.`in`.instructor.query.SearchInstructorsCriteria
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.FindInstructorByIdUseCase
import com.soomsoom.backend.application.port.`in`.instructor.usecase.query.SearchInstructorUseCase
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.application.port.out.mission.MissionCompletionLogPort
import com.soomsoom.backend.application.port.out.mission.MissionPort
import com.soomsoom.backend.common.event.Event
import com.soomsoom.backend.common.event.EventType
import com.soomsoom.backend.common.event.payload.PageVisitedPayload
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.common.DeletionStatus
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import com.soomsoom.backend.domain.mission.model.enums.MissionType
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FindInstructorService(
    private val instructorPort: InstructorPort,
    private val missionPort: MissionPort,
    private val missionCompletionLogPort: MissionCompletionLogPort,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${mission.page-visit.identifier.yawoongi}") private val yawoongiPageIdentifier: String,
) : FindInstructorByIdUseCase, SearchInstructorUseCase {

    companion object {
        private const val CHARACTER_NAME: String = "야웅이"
    }
    private val logger = org.slf4j.LoggerFactory.getLogger(javaClass)

    /**
     * ID로 강사 한 명을 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #deletionStatus.name() == 'ACTIVE'")
    override fun findById(instructorId: Long, userId: Long, deletionStatus: DeletionStatus): FindInstructorResult {
        val dto = instructorPort.findWithFollowStatusById(instructorId, userId, deletionStatus)
            ?: throw SoomSoomException(InstructorErrorCode.NOT_FOUND)

        // DTO 내부의 엔티티를 도메인 객체로 변환
        val instructor = dto.instructor.toDomain()

        var rewardableMissionInfo: FindInstructorResult.RewardableMissionInfo? = null

        // 조회한 강사가 '야웅이'인지 확인
        if (instructor.name == CHARACTER_NAME) {
            // '페이지 첫 방문' 타입의 미션 찾기
            val mission = missionPort.findByType(MissionType.FIRST_PAGE_VISIT)
            if (mission != null) {
                // 이 사용자가 해당 미션을 아직 완료하지 않았는지 확인
                val isCompleted = missionCompletionLogPort.exists(userId, mission.id)
                val isUnRewarded = missionCompletionLogPort.existsWithUnrewarded(userId, mission.id)

                if (!isCompleted || isUnRewarded) {
                    // 완료하지 않았다면, 응답에 보상 가능 정보를 담음
                    rewardableMissionInfo = FindInstructorResult.RewardableMissionInfo(missionId = mission.id, title = mission.title)
                    // 비동기로 미션 완료 처리를 요청합니다.
                    val visitEvent = Event(
                        eventType = EventType.PAGE_VISITED, // 새로운 EventType 추가 필요
                        payload = PageVisitedPayload(
                            userId = userId,
                            pageIdentifier = yawoongiPageIdentifier
                        )
                    )
                    eventPublisher.publishEvent(visitEvent)
                }
            }
        }

        // 도메인 객체와 팔로우 상태를 조합하여 최종 Result DTO를 생성하고 반환
        return FindInstructorResult.from(instructor, dto.isFollowing, rewardableMissionInfo)
    }

    /**
     * 검색 조건을 통해 여러 강사 조회
     */
    @PreAuthorize("hasRole('ADMIN') or #criteria.deletionStatus.name == 'ACTIVE'")
    override fun search(criteria: SearchInstructorsCriteria, pageable: Pageable): Page<FindInstructorResult> {
        val instructorDtoPage = instructorPort.searchWithFollowStatus(criteria, pageable)

        // DTO 페이지를 최종 Result DTO 페이지로 변환하여 반환
        return instructorDtoPage.map { dto ->
            val instructor = dto.instructor.toDomain()
            FindInstructorResult.from(instructor, dto.isFollowing, null)
        }
    }
}
