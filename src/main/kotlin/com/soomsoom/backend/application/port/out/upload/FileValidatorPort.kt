package com.soomsoom.backend.application.port.out.upload

interface FileValidatorPort {
    fun validate(fileKey: String): Boolean
}
