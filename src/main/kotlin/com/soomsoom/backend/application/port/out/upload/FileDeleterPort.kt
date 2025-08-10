package com.soomsoom.backend.application.port.out.upload

interface FileDeleterPort {
    fun delete(fileKey: String)
}
