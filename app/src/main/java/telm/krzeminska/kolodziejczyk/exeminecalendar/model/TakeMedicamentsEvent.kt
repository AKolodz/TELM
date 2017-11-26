package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by Acer on 19.11.2017.
 */
data class TakeMedicamentsEvent(override var id: Long?,
                                override var name: String,          //medicament name
                                override var description: String,   //doses
                                override var dateTime: LocalDateTime?,
                                override var reminders: Pair<ReminderType, MutableList<LocalDateTime>>?)
    : Event(id, name, description, dateTime, reminders) {

    override fun toString(): String = "$dateTime: Get $name"
}