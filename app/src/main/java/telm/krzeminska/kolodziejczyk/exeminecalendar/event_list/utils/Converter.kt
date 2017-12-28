package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.utils

import android.content.ContentValues
import android.net.Uri
import android.provider.CalendarContract
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.ReminderType
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.TimeToEvent
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Created by aleksander on 29.11.17.
 */
class Converter {
    fun uriToEventId(uri: Uri?): Long? = uri?.lastPathSegment?.toLong()

    fun reminderTypeToMethod(reminderType: ReminderType): Int =
            when (reminderType) {
                ReminderType.MAIL -> CalendarContract.Reminders.METHOD_EMAIL
                ReminderType.SMS -> CalendarContract.Reminders.METHOD_SMS
                ReminderType.ALARM -> CalendarContract.Reminders.METHOD_ALERT
                else -> throw IllegalArgumentException("Unknown reminder type")
            }

    fun timeToMinutes(time: TimeToEvent): Int =
            time.days * 24 * 60 + time.hours * 60 + time.minutes

    fun rruleToDurationDays(duration: String): Int =
            duration
                    .substringAfter("COUNT=")
                    .toInt()

    fun toLocalDateTime(millis: Long): LocalDateTime {
        val cal = Calendar.getInstance()
                .apply {
                    this.timeInMillis = millis
                }
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneId.of("Europe/Warsaw"))
    }

    fun eventToValues(event: Event, calendarId: Long): ContentValues {
        val values = ContentValues()
        val startMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue - 1,
                            event.dateTime.dayOfMonth,
                            event.dateTime.hour,
                            event.dateTime.minute)
                }.timeInMillis
        val endMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue - 1,
                            event.dateTime.dayOfMonth,
                            event.dateTime.hour + 1,
                            event.dateTime.minute)
                }.timeInMillis

        values.put(CalendarContract.Events.TITLE, event.name)
        values.put(CalendarContract.Events.DESCRIPTION, event.description)
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw")
        if (event.durationDays != null) {
            values.put(CalendarContract.Events.DURATION, "PT1H")
            values.put(CalendarContract.Events.RRULE, "FREQ=DAILY;INTERVAL=1;COUNT=${event.durationDays}")
        } else
            values.put(CalendarContract.Events.DTEND, endMillis)
        return values
    }

    fun minutesToTimeBeforeEvent(minutes : Int): TimeToEvent {
        //TODO(" Function needs implementation")
        return TimeToEvent()
    }
}