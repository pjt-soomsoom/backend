package com.soomsoom.backend.adapter.out.s3

import com.soomsoom.backend.application.port.out.upload.FileUrlResolverPort
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class S3FileUrlResolverAdapter(
    @Value("\${cloud.aws.s3.base-url}")
    private val baseUrl: String,
) : FileUrlResolverPort {

    override fun resolve(fileKey: String): String {
        return "${baseUrl.removeSuffix("/")}/${fileKey.removeSuffix("/")}"
    }
}
