package com.soomsoom.backend.application.port.out.instructor

import com.soomsoom.backend.domain.instructor.model.Instructor

interface InstructorPort {
    fun findById(id: Long): Instructor?
    fun findAll(): List<Instructor>
    fun save(instructor: Instructor): Instructor
}
