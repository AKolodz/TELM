package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.CalendarContract
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp.EventListMVP
import telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp.EventListPresenter
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import android.content.Intent



class MainActivity : AppCompatActivity(), EventListMVP.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

//        val presenter = EventListPresenter(this)
//        presenter.getEvents()
        loadCalendar()
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun showEvents(events: MutableList<Event>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

        val permissions : Array<String> = arrayOf(Manifest.permission.READ_CALENDAR)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat
                    .requestPermissions(this,  permissions, 111)
        }
// Submit the query and get a Cursor object back.
        var cursor: Cursor = contentResolver.query(uri, EVENT_PROJECTION, CalendarContract.Calendars.VISIBLE+" = 1", null, CalendarContract.Calendars._ID + " ASC")
        while (cursor.moveToNext()){
            var calID: Long = 0
            var displayName: String? = null
            var accountName: String? = null
            var ownerName: String? = null


            // Get the field values
            calID = cursor.getLong(PROJECTION_ID_INDEX);
            displayName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
            accountName = cursor.getString(PROJECTION_ACCOUNT_NAME_INDEX);
            ownerName = cursor.getString(PROJECTION_OWNER_ACCOUNT_INDEX);
        }
        val intent = Intent(Intent.ACTION_INSERT)
        intent.data = CalendarContract.Events.CONTENT_URI
        startActivity(intent)
    }
}
