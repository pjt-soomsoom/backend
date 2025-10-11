package com.soomsoom.backend.application.service.diary.command

import com.soomsoom.backend.application.port.`in`.diary.command.DeleteDiaryCommand
import com.soomsoom.backend.application.port.`in`.diary.usecase.command.DeleteDiaryUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.diary.DiaryErrorCode
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class DeleteDiaryService(
    private val diaryPort: DiaryPort,
) : DeleteDiaryUseCase {

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #command.principalId")
    override fun softDelete(command: DeleteDiaryCommand) {
        val diary = diaryPort.findById(command.diaryId)
            ?.also {
                it.delete()
            }
            ?: throw SoomSoomException(DiaryErrorCode.NOT_FOUND)

        validateOwnership(diary.userId, command.principalId)
        diaryPort.save(diary)
    }

    override fun hardDelete(command: DeleteDiaryCommand) {
        TODO("Not yet implemented")
    }

    @PreAuthorize("hasRole('ADMIN') or authentication.principal.id == #userId")
    override fun deleteByUserId(userId: Long) {
        diaryPort.deleteByUserId(userId)
    }

    private fun validateOwnership(ownerId: Long, principalId: Long) {
        val authentication = SecurityContextHolder.getContext().authentication
        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        // 관리자가 아니고, 일기의 소유주도 아니라면 예외 발생
        if (!isAdmin && ownerId != principalId) {
            throw SoomSoomException(DiaryErrorCode.FORBIDDEN)
        }
    }
}
