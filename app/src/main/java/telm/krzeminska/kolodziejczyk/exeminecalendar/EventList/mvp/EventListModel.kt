package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import android.provider.CalendarContract.Events
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.EventType
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import android.provider.SyncStateContract.Helpers.update




/**
 * Created by aleksander on 22.11.17.
 */
class EventListModel(private val applicationContext: Context) : EventListMVP.Model {

    private var calendarId = -1L
    private var eventList = mutableListOf<Event>()

    init {
        findCalendarId().apply {
            calendarId =
                    if (this != -1L)
                        this
                    else {
                        createMedicalCalendar()
                        findCalendarId()
                    }
        }
        //saveEvent(Event(null, "To update", "To update", LocalDateTime.now(), eventType = EventType.EXAMINATION))
        //saveEvent(Event(null, "Duration!","Duration!", LocalDateTime.now(),1,eventType = EventType.MEDICAMENT))
        deleteEvent(109)
    }

    @SuppressLint("MissingPermission")
    private fun findCalendarId(): Long {
        var foundId = -1L
        val mProjection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.ACCOUNT_NAME,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME)

        val cursor = applicationContext.contentResolver
                .query(CalendarContract.Calendars.CONTENT_URI,
                        mProjection,
                        CalendarContract.Calendars.VISIBLE + " = 1",
                        null,
                        CalendarContract.Calendars._ID + " ASC")

        while (cursor.moveToNext()) {
            val dispName = cursor.getString(cursor.getColumnIndex(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME))
            val accName = cursor.getString(cursor.getColumnIndex(Calendars.ACCOUNT_NAME))
            if (dispName == "MedicalEventsCalendar" && accName == "MedicalEventsCalendar")
                foundId = cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID))
        }
        return foundId
    }

    private fun createMedicalCalendar() {
        val values = ContentValues()
        values.put(Calendars.ACCOUNT_NAME, "MedicalEventsCalendar")
        values.put(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        values.put(Calendars.NAME, "MedicalEventsCalendar")
        values.put(Calendars.CALENDAR_DISPLAY_NAME, "MedicalEventsCalendar")
        values.put(Calendars.CALENDAR_COLOR, -0x10000)
        values.put(Calendars.CALENDAR_ACCESS_LEVEL, Calendars.CAL_ACCESS_OWNER)
        values.put(Calendars.OWNER_ACCOUNT, "owner.account@googlemail.com")
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Warsaw")
        values.put(Calendars.SYNC_EVENTS, 1)

        val builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
        builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "MedicalEventsCalendar")
        builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")

        val uri = applicationContext.contentResolver.insert(builder.build(), values)
    }

    @SuppressLint("MissingPermission")
    private fun saveEvent(event: Event) {
        val values = ContentValues()
        val startMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue-1,
                            event.dateTime.dayOfMonth,
                            event.dateTime.hour,
                            event.dateTime.minute)
                }.timeInMillis
        val endMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue-1,
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
            values.put(CalendarContract.Events.DURATION, "PT${event.durationDays}H")
            values.put(Events.RRULE, "FREQ=DAILY;INTERVAL=1;COUNT=7")
        }
        else
            values.put(CalendarContract.Events.DTEND, endMillis)
//        if (event.reminders!=null)
//            //TODO: Add reminder
        val uri = applicationContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
    }

    @SuppressLint("MissingPermission")
    private fun deleteEvent(id: Long) =
        applicationContext.contentResolver.delete(Events.CONTENT_URI,
                Events._ID + " = $id",
                null)

    @SuppressLint("MissingPermission")
    private fun updateEvent(updatedId: Long, newEvent: Event) {
        val values = ContentValues()
        val startMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            newEvent.dateTime.year,
                            newEvent.dateTime.monthValue,
                            newEvent.dateTime.dayOfMonth,
                            newEvent.dateTime.hour,
                            newEvent.dateTime.minute)
                }.timeInMillis
        val endMillis = Calendar.getInstance()
                .apply {
                    this.set(
                            newEvent.dateTime.year,
                            newEvent.dateTime.monthValue,
                            newEvent.dateTime.dayOfMonth,
                            newEvent.dateTime.hour + 1,
                            newEvent.dateTime.minute)
                }.timeInMillis

        values.put(CalendarContract.Events.TITLE, newEvent.name)
        values.put(CalendarContract.Events.DESCRIPTION, newEvent.description)
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Warsaw")
        if (newEvent.durationDays != null)
            values.put(CalendarContract.Events.DURATION, "PT${newEvent.durationDays}D")
        else
            values.put(CalendarContract.Events.DTEND, endMillis)
//        if (event.reminders!=null)
//            //TODO: Add reminder

        val updated = applicationContext.contentResolver.update(Events.CONTENT_URI,
                values,
                Events._ID + " = $updatedId",
                null)
    }

    @SuppressLint("MissingPermission")
    override fun getAllEvents(): MutableList<Event> {
        val mProjection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DURATION)

        val cursor: Cursor = applicationContext.contentResolver.query(CalendarContract.Events.CONTENT_URI,
                mProjection,                                                                        //what query requests for
                CalendarContract.Events.CALENDAR_ID + " = ? ",                              //query
                arrayOf("$calendarId"),                                                             //query values
                null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID)).toLong()
            val name = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE))
            val description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION))
            val start = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART)).toLong()
            val duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DURATION))
            val dateTime = toLocalDateTime(start)

            if (duration == null) {
                eventList.add(Event(id, name, description, dateTime, duration, null, EventType.EXAMINATION))
            } else {
                eventList.add(Event(id, name, description, dateTime, rfcToInt(duration), null, EventType.MEDICAMENT))
            }
        }
        return eventList
    }

    private fun rfcToInt(duration: String): Int =
            duration
                    .removeSuffix("D")
                    .removePrefix("PT")
                    .removeSuffix("H")
                    .toInt()


    private fun toLocalDateTime(millis: Long): LocalDateTime {
        val cal = Calendar.getInstance()
                .apply {
                    this.timeInMillis = millis
                }
        return LocalDateTime.ofInstant(cal.toInstant(), ZoneId.of("Europe/Warsaw"))
    }
}