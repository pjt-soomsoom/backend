package com.soomsoom.backend.application.port.`in`.item.command.collection

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class CreateCollectionCommand(
    val name: String,
    val description: String?,
    val phrase: String?,
    val image: ValidatedFileMetadata,
    val lottie: ValidatedFileMetadata?,
    val itemIds: List<Long>,
)
