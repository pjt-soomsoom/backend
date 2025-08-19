package com.soomsoom.backend.domain.activity.model

class TimelineEvent(
    val id: Long?,
    var time: Double,
    var action: String,
    var text: String,
    var duration: Double? = null,
) {
    fun update(time: Double, action: String, text: String, duration: Double?) {
        this.time = time
        this.action = action
        this.text = text
        this.duration = duration
    }
}
