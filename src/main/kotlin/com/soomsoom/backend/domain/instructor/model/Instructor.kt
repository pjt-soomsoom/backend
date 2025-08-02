package com.soomsoom.backend.domain.instructor.model

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode

class Instructor(
    val id: Long? = null,
    name: String,
    bio: String?,
    profileImageUrl: String? = null,
) {
    var name: String = name
        private set

    var bio: String? = bio
        private set

    var profileImageUrl: String? = profileImageUrl
        private set

    fun updateProfileImageUrl(url: String) {
        this.profileImageUrl = url
    }

    fun updateInfo(name: String, bio: String?) {
        if (name.isBlank()) {
            throw SoomSoomException(InstructorErrorCode.NAME_CANNOT_BE_BLANK)
        }
        this.name = name
        this.bio = bio
    }
}
