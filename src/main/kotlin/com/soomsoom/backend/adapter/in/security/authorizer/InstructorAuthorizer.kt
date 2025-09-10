package com.soomsoom.backend.adapter.`in`.security.authorizer

import com.soomsoom.backend.adapter.`in`.security.service.CustomUserDetails
import com.soomsoom.backend.application.port.out.instructor.InstructorPort
import com.soomsoom.backend.domain.user.model.aggregate.Role
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

@Component("instructorAuthorizer")
class InstructorAuthorizer(
    private val instructorPort: InstructorPort,
) {

    fun isAdmin(authentication: Authentication): Boolean {
        val userDetails = authentication.principal as? CustomUserDetails
            ?: return false

        return userDetails.authorities.any { it.authority == Role.ROLE_ADMIN.name }
    }
}
