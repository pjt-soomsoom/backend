package com.soomsoom.backend.application.port.`in`.item.usecase.command

import com.soomsoom.backend.application.port.`in`.item.command.CreateCollectionCommand

interface CreateCollectionUseCase {
    fun createCollection(command: CreateCollectionCommand): Long
}
