package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by aleksander on 19.11.17.
 */
data class ExaminationEvent(override var id: Long? = null,
                            override var name: String,          // specialist name
                            override var description: String,   // location
                            override var dateTime: LocalDateTime? = null,
                            override var reminders: Pair<ReminderType, MutableList<LocalDateTime>>? = null)
    : Event(id, name, description, dateTime, reminders) {

    override fun toString(): String = "$dateTime: Visit $name at $description"
}

