package com.soomsoom.backend.application.service.diary.query

import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.query.SearchDiariesCriteria
import com.soomsoom.backend.application.port.`in`.diary.usecase.query.SearchDiariesUseCase
import com.soomsoom.backend.application.port.out.diary.DiaryPort
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PostAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchDiariesService(
    private val diaryPort: DiaryPort,
) : SearchDiariesUseCase {

    @PostAuthorize("hasRole('ADMIN') or (#criteria.userId == authentication.principal.id and #criteria.deletionStatus.name() == 'ACTIVE')")
    override fun search(criteria: SearchDiariesCriteria, pageable: Pageable): Page<FindDiaryResult> {
        val diaryPage = diaryPort.search(criteria, pageable)
        return diaryPage.map { FindDiaryResult.from(it) }
    }
}
