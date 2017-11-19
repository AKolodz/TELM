package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by aleksander on 19.11.17.
 */
data class ExaminationEvent(var examinationName: String,
                            var place: String,
                            var reminders: Pair<ReminderType, MutableList<LocalDateTime>>) :Event

