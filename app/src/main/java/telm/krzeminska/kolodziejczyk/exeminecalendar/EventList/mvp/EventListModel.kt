package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.EventType
import java.time.LocalDateTime
import java.util.*


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
        addEvent(Event(null, "Id test", "Test Id", LocalDateTime.now().plusDays(4), eventType = EventType.MEDICAMENT))
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
        values.put(Calendars.CALENDAR_TIME_ZONE, "Europe/Berlin")
        values.put(Calendars.SYNC_EVENTS, 1)

        val builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
        builder.appendQueryParameter(Calendars.ACCOUNT_NAME, "MedicalEventsCalendar")
        builder.appendQueryParameter(Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        builder.appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")

        val uri = applicationContext.contentResolver.insert(builder.build(), values)
    }

    @SuppressLint("MissingPermission")
    private fun addEvent(event: Event) {
        val cr = applicationContext.contentResolver
        val values = ContentValues()
        val beginTime: Calendar = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue,
                            event.dateTime.dayOfMonth,
                            event.dateTime.hour,
                            event.dateTime.minute)
                }
        val endTime = Calendar.getInstance()
                .apply {
                    this.set(
                            event.dateTime.year,
                            event.dateTime.monthValue,
                            event.dateTime.dayOfMonth,
                            event.dateTime.hour + 1,
                            event.dateTime.minute)
                }

        val startMillis = beginTime.timeInMillis
        val endMillis = endTime.timeInMillis

        values.put(CalendarContract.Events.TITLE, event.name)
        values.put(CalendarContract.Events.DESCRIPTION, event.description)
        values.put(CalendarContract.Events.DTSTART, startMillis)
        if (event.durationDays != null)
            values.put(CalendarContract.Events.DURATION, "PT${event.durationDays}D")
        else
            values.put(CalendarContract.Events.DTEND, endMillis)
        values.put(CalendarContract.Events.CALENDAR_ID, calendarId)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin")
//        if (event.reminders!=null)
//            //TODO: Add reminder

        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
//     val eventID: Long = uri.lastPathSegment.toLong()
    }

    @SuppressLint("MissingPermission")
    override fun getAllEvents(): MutableList<Event> {
        val cr = applicationContext.contentResolver

        val mProjection = arrayOf(
                CalendarContract.Events._ID,
                CalendarContract.Events.TITLE,
                CalendarContract.Events.DESCRIPTION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND,
                CalendarContract.Events.DURATION)

        val cursor: Cursor = cr.query(CalendarContract.Events.CONTENT_URI,
                mProjection,
                CalendarContract.Events.CALENDAR_ID + " = ? ",                              //query
                arrayOf("$calendarId"),                                                             //query values
                null)

        while (cursor.moveToNext()) {
            val id = cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID)).toLong()
            val name = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE))
            val description = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DESCRIPTION))
            val start = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTSTART))
            val end = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DTEND))
            val duration = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.DURATION))

            if (duration == null)
                eventList.add(Event(id, name, description, LocalDateTime.now(), eventType = EventType.EXAMINATION))
            else
                eventList.add(Event(id, name, description, LocalDateTime.now(), eventType = EventType.MEDICAMENT))
        }
        return eventList
    }
}