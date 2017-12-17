package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListMVP
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListPresenter
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event


class MainActivity : AppCompatActivity(), EventListMVP.View {

    private val CALENDAR_PERMISSION_REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            pullEventsList()
        else
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                    CALENDAR_PERMISSION_REQUESTCODE)


        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    private fun pullEventsList() {
        val presenter = EventListPresenter(this, applicationContext)
        presenter.getEvents()
    }

    override fun showInfo(message: String) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun showEvents(events: MutableList<Event>) {
        //TODO
        Toast.makeText(this, "Show", Toast.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALENDAR_PERMISSION_REQUESTCODE ->
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission must be granted", Toast.LENGTH_SHORT).show()
                    this.finish()
                } else
                    pullEventsList()
        }
    }
}

//            startActivity(intent)
//            intent.data = CalendarContract.Events.CONTENT_URI
//            val intent = Intent(Intent.ACTION_INSERT)