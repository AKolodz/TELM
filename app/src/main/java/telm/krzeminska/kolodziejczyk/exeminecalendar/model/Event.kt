package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by Acer on 19.11.2017.
 */
abstract class Event(
        open var id: Long?,
        open var name: String,
        open var description: String,
        open var dateTime: LocalDateTime?,
        open var reminders: Pair<ReminderType, MutableList<LocalDateTime>>?) {

    override fun toString(): String = "Event"
}
