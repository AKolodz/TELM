package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.Manifest
import android.app.Activity
import android.content.ContentResolver
import android.content.pm.PackageManager
import android.database.Cursor
import android.provider.CalendarContract.Calendars
import android.support.v4.app.ActivityCompat
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event


/**
 * Created by aleksander on 22.11.17.
 */
class EventListModel : EventListMVP.Model {


    init {

    }

    override fun getEvents(): MutableList<Event> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}