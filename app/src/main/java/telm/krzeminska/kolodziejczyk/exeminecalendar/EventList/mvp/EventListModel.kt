package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.Manifest
import android.content.Context
import android.database.Cursor
import android.provider.CalendarContract
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event


/**
 * Created by aleksander on 22.11.17.
 */
class EventListModel(private val applicationContext: Context) : EventListMVP.Model {


    init {
        loadCalendar()
    }

    private fun loadCalendar() {
        val EVENT_PROJECTION = arrayOf(
                CalendarContract.Calendars._ID,                        // 0
                CalendarContract.Calendars.ACCOUNT_NAME,               // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,      // 2
                CalendarContract.Calendars.OWNER_ACCOUNT               // 3
        )

        val PROJECTION_ID_INDEX = 0
        val PROJECTION_ACCOUNT_NAME_INDEX = 1
        val PROJECTION_DISPLAY_NAME_INDEX = 2
        val PROJECTION_OWNER_ACCOUNT_INDEX = 3

        val uri = CalendarContract.Calendars.CONTENT_URI
        val selection: String = ("(("
                + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?) AND ("
                + CalendarContract.Calendars.OWNER_ACCOUNT + " = ?))")
        val selectionArgs = arrayOf("a.t.kolodziejczyk@gmail.com", "a.t.kolodziejczyk.gmail.com", "a.t.kolodziejczyk@gmail.com")

        val permissions: Array<String> = arrayOf(Manifest.permission.READ_CALENDAR)
// Submit the query and get a Cursor object back.
        var cursor: Cursor = applicationContext.contentResolver
                .query(uri, EVENT_PROJECTION, CalendarContract.Calendars.VISIBLE + " = 1", null, CalendarContract.Calendars._ID + " ASC")
        while (cursor.moveToNext()) {
            var calID: Long = 0
            var displayName: String? = null
            var accountName: String? = null
            var ownerName: String? = null


            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX)
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX)
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX)
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX)


        }
    }


    override fun getEvents(): MutableList<Event> {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return mutableListOf()
    }
}