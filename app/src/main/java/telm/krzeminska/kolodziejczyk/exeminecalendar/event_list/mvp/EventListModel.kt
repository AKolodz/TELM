package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import android.provider.CalendarContract.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.utils.Converter
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.EventType
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.ReminderType
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.TimeToEvent
import java.time.LocalDateTime


/**
 * Created by aleksander on 22.11.17.
 */
class EventListModel(private val applicationContext: Context) : EventListMVP.Model {

    private var calendarId = -1L
    private var eventList = mutableListOf<Event>()
    private var converter = Converter()

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
        //saveEvent(Event(null, "Test Event", "Test event", LocalDateTime.now().plusMinutes(6), null, Pair(ReminderType.ALARM, TimeToEvent(minutes = 5)) , EventType.EXAMINATION))
        //saveEvent(Event(null, "NEW!", "NEW!", LocalDateTime.now(), 3, Pair(ReminderType.ALARM, TimeToEvent(minutes = 5)), EventType.MEDICAMENT))
//        deleteEvent(138)
        //updateEvent(137, Event(null, "Updated", "UPDATED", LocalDateTime.now().plusMinutes(6), null,Pair(ReminderType.ALARM, TimeToEvent(minutes = 5)), EventType.EXAMINATION))
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

        applicationContext.contentResolver.insert(builder.build(), values)
    }

    @SuppressLint("MissingPermission")
    override fun saveEvent(event: Event) {
        converter.eventToValues(event, calendarId)
                .apply {
                    applicationContext.contentResolver.insert(CalendarContract.Events.CONTENT_URI, this)
                            .apply {
                                setReminders(converter.uriToEventId(this), event.reminder)
                            }
                }
    }

    @SuppressLint("MissingPermission")
    override fun updateEvent(idToUpdate: Long, newEvent: Event) {
        converter.eventToValues(newEvent, calendarId)
                .apply {
                    applicationContext.contentResolver.update(
                            Events.CONTENT_URI,
                            this,
                            Events._ID + " = $idToUpdate",
                            null)
                            .apply {
                                deleteReminders(idToUpdate)
                                setReminders(idToUpdate, newEvent.reminder)
                            }
                }
    }

    @SuppressLint("MissingPermission")
    private fun deleteReminders(idToUpdate: Long) {
        applicationContext.contentResolver.delete(Reminders.CONTENT_URI,
                Reminders.EVENT_ID + " = $idToUpdate",
                null)
    }

    @SuppressLint("MissingPermission")
    private fun setReminders(id: Long?, reminder: Pair<ReminderType, TimeToEvent>?) {
        if (reminder == null || id == null || reminder.first == ReminderType.NONE)
            return

        val values = ContentValues()
        val minutes = converter.timeToMinutes(reminder.second)
        val method = converter.reminderTypeToMethod(reminder.first)

        values.put(Reminders.MINUTES, minutes)
        values.put(Reminders.EVENT_ID, id)
        values.put(Reminders.METHOD, method)
        applicationContext.contentResolver.insert(Reminders.CONTENT_URI, values)
    }

    @SuppressLint("MissingPermission")
    override fun deleteEvent(id: Long) {
        applicationContext.contentResolver.delete(Events.CONTENT_URI,
                Events._ID + " = $id",
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
                CalendarContract.Events.RRULE)

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
            val rrule = cursor.getString(cursor.getColumnIndex(CalendarContract.Events.RRULE))
            val dateTime = converter.toLocalDateTime(start)
            val reminder = getReminders(id)

            if (rrule == null) {
                eventList.add(Event(id, name, description, dateTime, null, reminder, EventType.EXAMINATION))
            } else {
                eventList.add(Event(id, name, description, dateTime, converter.rruleToDurationDays(rrule), reminder, EventType.MEDICAMENT))
            }
        }
        return eventList
    }

    @SuppressLint("MissingPermission")
    private fun getReminders(eventId: Long): Pair<ReminderType, TimeToEvent> {
        val types = mapOf(
                "1" to ReminderType.ALARM,
                "2" to ReminderType.MAIL,
                "3" to ReminderType.SMS
        )
        var type = ReminderType.NONE
        var timeToEvent = TimeToEvent()
        val mProjection = arrayOf(
                CalendarContract.Reminders.METHOD,
                CalendarContract.Reminders.MINUTES)

        val cursor: Cursor = applicationContext.contentResolver.query(CalendarContract.Reminders.CONTENT_URI,
                mProjection,
                CalendarContract.Reminders.EVENT_ID + " = ? ",
                arrayOf("$eventId"),
                null)

        while (cursor.moveToNext()) {
            type = cursor.getString(cursor.getColumnIndex(CalendarContract.Reminders.METHOD))
                    .run {
                        types[this] ?: ReminderType.NONE
                    }
            timeToEvent = cursor.getString(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES)).toInt()
                    .run {
                        val days = (this / (24 * 60)) //round it down
                        val hours = (this.rem(24 * 60) / 60)
                        val minutes = (this.rem(24 * 60)).rem(60)
                        TimeToEvent(days, hours, minutes)
                    }
        }
        return Pair(type, timeToEvent)
    }
}