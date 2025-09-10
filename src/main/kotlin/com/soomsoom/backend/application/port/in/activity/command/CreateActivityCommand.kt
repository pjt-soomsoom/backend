package com.soomsoom.backend.application.port.`in`.activity.command

import com.soomsoom.backend.application.port.`in`.upload.command.ValidatedFileMetadata
import com.soomsoom.backend.domain.activity.model.TimelineEvent
import com.soomsoom.backend.domain.activity.model.enums.ActivityCategory

sealed class CreateActivityCommand {
    abstract val title: String
    abstract val descriptions: List<String>
    abstract val category: ActivityCategory
    abstract val authorId: Long
    abstract val narratorId: Long
    abstract val durationInSeconds: Int
    abstract val thumbnailImageMetadata: ValidatedFileMetadata
    abstract val audioMetadata: ValidatedFileMetadata
}

data class CreateBreathingActivityCommand(
    override val title: String,
    override val descriptions: List<String>,
    override val category: ActivityCategory,
    override val authorId: Long,
    override val narratorId: Long,
    override val durationInSeconds: Int,
    override val thumbnailImageMetadata: ValidatedFileMetadata,
    override val audioMetadata: ValidatedFileMetadata,
    val timeline: List<TimelineEvent>,
) : CreateActivityCommand()

data class CreateMeditationActivityCommand(
    override val title: String,
    override val descriptions: List<String>,
    override val category: ActivityCategory,
    override val authorId: Long,
    override val narratorId: Long,
    override val durationInSeconds: Int,
    override val thumbnailImageMetadata: ValidatedFileMetadata,
    override val audioMetadata: ValidatedFileMetadata,
) : CreateActivityCommand()

data class CreateSoundEffectActivityCommand(
    override val title: String,
    override val descriptions: List<String>,
    override val category: ActivityCategory,
    override val authorId: Long,
    override val narratorId: Long,
    override val durationInSeconds: Int,
    override val thumbnailImageMetadata: ValidatedFileMetadata,
    override val audioMetadata: ValidatedFileMetadata,
) : CreateActivityCommand()
