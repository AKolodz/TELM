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
        var reminder: Pair<ReminderType, TimeToEvent>? = null,
        private var eventType: EventType) {

    override fun toString(): String =
            when (eventType) {
                EventType.EXAMINATION -> ""
                EventType.MEDICAMENT -> ""
            }
}

data class TimeToEvent(
        var days: Int = 0,
        var hours: Int = 0,
        var minutes: Int = 0)

enum class EventType {
    EXAMINATION,
    MEDICAMENT
}
