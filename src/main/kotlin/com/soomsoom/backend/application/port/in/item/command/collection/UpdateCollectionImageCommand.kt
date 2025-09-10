package com.soomsoom.backend.application.port.`in`.item.command.collection

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateCollectionImageCommand(
    val collectionId: Long,
    val imageMetadata: ValidatedFileMetadata,
)
