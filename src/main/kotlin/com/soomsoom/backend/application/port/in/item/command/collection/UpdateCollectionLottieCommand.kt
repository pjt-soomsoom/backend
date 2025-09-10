package com.soomsoom.backend.application.port.`in`.item.command.collection

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata

data class UpdateCollectionLottieCommand(
    val collectionId: Long,
    val lottieMetadata: ValidatedFileMetadata?,
)
