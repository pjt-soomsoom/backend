package com.soomsoom.backend.application.port.`in`.diary.usecase.query

import com.soomsoom.backend.application.port.`in`.diary.dto.FindDiaryResult
import com.soomsoom.backend.application.port.`in`.diary.query.SearchDiariesCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface SearchDiariesUseCase {
    fun search(criteria: SearchDiariesCriteria, pageable: Pageable): Page<FindDiaryResult>
}
