package com.soomsoom.backend.application.port.out.upload

interface FileUrlResolverPort {
    fun resolve(fileKey: String): String
}
