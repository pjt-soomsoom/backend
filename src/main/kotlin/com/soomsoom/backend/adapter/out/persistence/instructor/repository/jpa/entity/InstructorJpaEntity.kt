package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity

import com.soomsoom.backend.common.entity.BaseTimeEntity
import com.soomsoom.backend.domain.instructor.model.Instructor
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "instructors")
class InstructorJpaEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(nullable = false)
    var name: String,

    @Column(columnDefinition = "TEXT")
    var bio: String?,

    @Column(columnDefinition = "TEXT")
    var profileImageUrl: String?,

    @Column(columnDefinition = "TEXT")
    var profileImageFileKey: String?,

) : BaseTimeEntity() {

    // 도메인 객체를 DB 엔티티로 변환
    companion object {
        fun from(instructor: Instructor): InstructorJpaEntity {
            return InstructorJpaEntity(
                instructor.id ?: 0L,
                instructor.name,
                instructor.bio,
                instructor.profileImageUrl,
                instructor.profileImageFileKey
            )
        }
    }

    fun toDomain(): Instructor {
        return Instructor(
            id = this.id,
            name = this.name,
            bio = this.bio,
            profileImageUrl = this.profileImageUrl,
            profileImageFileKey = this.profileImageFileKey,
            createdAt = this.createdAt,
            modifiedAt = this.modifiedAt,
            deletedAt = this.deletedAt
        )
    }

    fun update(instructor: Instructor) {
        this.name = instructor.name
        this.bio = instructor.bio
        this.profileImageUrl = instructor.profileImageUrl
        this.profileImageFileKey = instructor.profileImageFileKey
        this.deletedAt = instructor.deletedAt
    }
}
