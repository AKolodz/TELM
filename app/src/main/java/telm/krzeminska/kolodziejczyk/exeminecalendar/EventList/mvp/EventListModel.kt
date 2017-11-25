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
        addEvent()
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
    private fun addEvent() {
        var calID = calendarId
        var startMillis = 0L
        var endMillis = 0L
        var beginTime = Calendar.getInstance()
        beginTime.set(2012, 9, 14, 7, 30);
        startMillis = beginTime.timeInMillis
        var endTime = Calendar.getInstance();
        endTime.set(2012, 9, 14, 8, 45);
        endMillis = endTime.timeInMillis;

        var cr = applicationContext.contentResolver
        var values = ContentValues()
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, "Jazzercise");
        values.put(CalendarContract.Events.DESCRIPTION, "Group workout");
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, "Europe/Berlin");
        var uri = cr.insert(CalendarContract.Events.CONTENT_URI, values)

// get the event ID that is the last element in the Uri
        var eventID: Long = uri.lastPathSegment.toLong()

        eventList.add(ExaminationEvent(eventID.toString(),eventID.toString(), Pair(ReminderType.ALARM, mutableListOf(LocalDateTime.MAX))))
//
// ... do something with event ID
//
//
    }

    override fun getEvents(): MutableList<Event> {
        eventList.add(ExaminationEvent("1","1", Pair(ReminderType.ALARM, mutableListOf(LocalDateTime.MAX))))
        return eventList
    }
}