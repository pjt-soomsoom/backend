package com.soomsoom.backend.application.port.`in`.item.usecase.command.collection

import com.soomsoom.backend.application.port.`in`.item.command.collection.CompleteCollectionLottieUpdateCommand
import com.soomsoom.backend.application.port.`in`.item.command.collection.UpdateCollectionLottieCommand
import com.soomsoom.backend.application.port.`in`.item.dto.UpdateCollectionFileResult

interface UpdateCollectionLottieUseCase {
    fun updateLottie(command: UpdateCollectionLottieCommand): UpdateCollectionFileResult
    fun completeLottieUpdate(command: CompleteCollectionLottieUpdateCommand)
    fun removeLottie(collectionId: Long)
}
