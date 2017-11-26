package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import android.provider.CalendarContract.Calendars
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.ExaminationEvent
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.ReminderType
import java.time.LocalDateTime
import java.util.*
import android.widget.TextView
import android.content.ContentResolver
import android.database.Cursor


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
        addEvent(ExaminationEvent(name = "ExaminationEvent",description = "Manually added"))
    }

    @SuppressLint("MissingPermission")
    private fun findCalendarId(): Long {
        var foundId = -1L
        val EVENT_PROJECTION = arrayOf(
                CalendarContract.Calendars._ID,                        // 0
                CalendarContract.Calendars.ACCOUNT_NAME,               // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME     // 2
        )
        val PROJECTION_ID_INDEX = 0
        val PROJECTION_ACCOUNT_NAME_INDEX = 1
        val PROJECTION_DISPLAY_NAME_INDEX = 2

        val cursor = applicationContext.contentResolver
                .query(CalendarContract.Calendars.CONTENT_URI,
                        EVENT_PROJECTION,
                        CalendarContract.Calendars.VISIBLE + " = 1",
                        null,
                        CalendarContract.Calendars._ID + " ASC")

        while (cursor.moveToNext()) {
            val dispName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
            val accName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            if (dispName == "MedicalEventsCalendar" && accName == "MedicalEventsCalendar")
                foundId = cursor.getLong(PROJECTION_ID_INDEX)
        }
        return foundId
    }

    private fun createMedicalCalendar() {
        val values = ContentValues()
        values.put(
                Calendars.ACCOUNT_NAME,
                "MedicalEventsCalendar")
        values.put(
                Calendars.ACCOUNT_TYPE,
                CalendarContract.ACCOUNT_TYPE_LOCAL)
        values.put(
                Calendars.NAME,
                "MedicalEventsCalendar")
        values.put(
                Calendars.CALENDAR_DISPLAY_NAME,
                "MedicalEventsCalendar")
        values.put(
                Calendars.CALENDAR_COLOR,
                -0x10000)
        values.put(
                Calendars.CALENDAR_ACCESS_LEVEL,
                Calendars.CAL_ACCESS_OWNER)
        values.put(
                Calendars.OWNER_ACCOUNT,
                "owner.account@googlemail.com")
        values.put(
                Calendars.CALENDAR_TIME_ZONE,
                "Europe/Berlin")
        values.put(
                Calendars.SYNC_EVENTS, 1)
        val builder = CalendarContract.Calendars.CONTENT_URI.buildUpon()
        builder.appendQueryParameter(
                Calendars.ACCOUNT_NAME, "MedicalEventsCalendar")
        builder.appendQueryParameter(
                Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
        builder.appendQueryParameter(
                CalendarContract.CALLER_IS_SYNCADAPTER, "true")
        val uri = applicationContext.contentResolver.insert(builder.build(), values)
    }

    @SuppressLint("MissingPermission")
    private fun addEvent(event: Event) {
        val calID = calendarId
        var startMillis = 0L
        var endMillis = 0L
        val beginTime = Calendar.getInstance()
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.timeInMillis
        val endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.timeInMillis;

        val cr = applicationContext.contentResolver
        val values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis)
        values.put(CalendarContract.Events.DTEND, endMillis)
        values.put(CalendarContract.Events.TITLE, event.name)
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout")
        values.put(CalendarContract.Events.CALENDAR_ID, calID)
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin")
        val uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)
// get the event ID that is the last element in the Uri
//      val eventID: Long = uri.lastPathSegment.toLong()
// ... do something with event ID
    }

    @SuppressLint("MissingPermission")
    override fun getEvents(): MutableList<Event> {
        val cr = applicationContext.contentResolver

        //I suppose: things I want to know
        val mProjection = arrayOf("_id",
                CalendarContract.Events.TITLE,
                CalendarContract.Events.EVENT_LOCATION,
                CalendarContract.Events.DTSTART,
                CalendarContract.Events.DTEND)

        val uri = CalendarContract.Events.CONTENT_URI
        //I suppose: query
        val selection = CalendarContract.Events.CALENDAR_ID + " = ? "
        //I suppose: query parameters
        val selectionArgs = arrayOf("$calendarId")

        var cursor: Cursor = cr.query(uri, mProjection, selection, selectionArgs, null)

        while (cursor.moveToNext()) {
            val name = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.TITLE))
            TODO("eventList.add()")
        }

        return eventList
    }
}