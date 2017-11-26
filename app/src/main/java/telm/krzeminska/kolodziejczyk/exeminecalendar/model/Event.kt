package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by Acer on 19.11.2017.
 */
class Event(
        var id: Long? = null,
        var name: String,
        var description: String,
        var dateTime: LocalDateTime,
        var durationDays: Int? = null,
        var reminders: Pair<ReminderType, MutableList<LocalDateTime>>? = null,
        var eventType: EventType) {

    override fun toString(): String =
            when (eventType) {
                EventType.EXAMINATION -> ""
                EventType.MEDICAMENT -> ""
            }
}

enum class EventType {
    EXAMINATION,
    MEDICAMENT
}
