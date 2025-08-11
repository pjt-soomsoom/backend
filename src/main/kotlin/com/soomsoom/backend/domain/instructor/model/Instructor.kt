package com.soomsoom.backend.domain.instructor.model

import com.soomsoom.backend.common.exception.SoomSoomException
import com.soomsoom.backend.domain.instructor.InstructorErrorCode
import java.time.LocalDateTime

class Instructor(
    val id: Long? = null,
    name: String,
    bio: String?,
    profileImageUrl: String? = null,
    profileImageFileKey: String? = null,

    val createdAt: LocalDateTime? = null,
    val modifiedAt: LocalDateTime? = null,
    deletedAt: LocalDateTime? = null,
) {
    var name: String = name
        private set

    var bio: String? = bio
        private set

    var profileImageUrl: String? = profileImageUrl
        private set

    var deletedAt: LocalDateTime? = deletedAt
        private set

    val isDeleted: Boolean
        get() = deletedAt != null

    var profileImageFileKey: String? = profileImageFileKey
        private set

    fun updateProfileImageUrl(url: String, fileKey: String): String? {
        val oldFileKey = this.profileImageFileKey
        this.profileImageUrl = url
        this.profileImageFileKey = fileKey
        return oldFileKey
    }

    fun updateInfo(name: String, bio: String?) {
        if (name.isBlank()) {
            throw SoomSoomException(InstructorErrorCode.NAME_CANNOT_BE_BLANK)
        }
        this.name = name
        this.bio = bio
    }

    fun delete() {
        deletedAt = LocalDateTime.now()
    }
}
