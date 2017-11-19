package telm.krzeminska.kolodziejczyk.exeminecalendar.model

import java.time.LocalDateTime

/**
 * Created by aleksander on 19.11.17.
 */
data class Examination(var dateTime : LocalDateTime,
                       var place : String,
                       var reminderType : ReminderType)