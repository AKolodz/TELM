package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by Acer on 19.11.2017.
 */
data class TakeMedicamentsEvent(
        var medicamentName: String,
        var dose: String,
        var reminders: Pair<ReminderType, MutableList<LocalDateTime>>
)