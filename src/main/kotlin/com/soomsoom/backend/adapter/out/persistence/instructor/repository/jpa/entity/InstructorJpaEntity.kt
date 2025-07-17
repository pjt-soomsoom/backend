package com.soomsoom.backend.adapter.out.persistence.instructor.repository.jpa.entity

import com.soomsoom.backend.domain.instructor.model.Instructor
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
    var name: String,
    var profileImageUrl: String?,
    var bio: String?,
) {

    // 도메인 객체를 DB 엔티티로 변환
    companion object {
        fun from(instructor: Instructor): InstructorJpaEntity {
            return InstructorJpaEntity(instructor.id ?: 0L, instructor.name, instructor.profileImageUrl, instructor.bio)
        }
    }

    fun toDomain(): Instructor {
        return Instructor(this.id, this.name, this.profileImageUrl, this.bio)
    }
}
