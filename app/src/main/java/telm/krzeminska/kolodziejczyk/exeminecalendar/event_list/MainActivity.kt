package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListMVP
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListPresenter
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.EventType
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), EventListMVP.View {

    private val CALENDAR_PERMISSION_REQUESTCODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(toolbar)

        var eventList = eventList
        val events: MutableList<Event> = mutableListOf(Event(1, "First",
                "First description", LocalDateTime.now(), eventType = EventType.EXAMINATION),
                Event(2, "Second", "Second descrption", LocalDateTime.now(), 5, eventType = EventType.MEDICAMENT))
        val adapter = EventAdapter(this, R.layout.rowlayout, events)
        eventList.adapter = adapter

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED)
            pullEventsList()
        else
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                    CALENDAR_PERMISSION_REQUESTCODE)


        eventList.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "Item $i", Toast.LENGTH_SHORT).show()
        }

        registerForContextMenu(eventList)



        fab.setOnClickListener {
            val dialogBox = AlertDialog.Builder(this).create()
            dialogBox.setTitle("Select the event you want to add");
            dialogBox.setButton(AlertDialog.BUTTON_POSITIVE, "Examination", { _, i ->
                Toast.makeText(applicationContext, "Examination", Toast.LENGTH_LONG).show()
                showExaminationDialog()
            })
            dialogBox.setButton(AlertDialog.BUTTON_NEUTRAL, "Medicaments", { _, i ->
                Toast.makeText(applicationContext, "Medicaments", Toast.LENGTH_LONG).show()
                showMedicamentDialog()
            })
            dialogBox.show()
        }
    }

    private fun showExaminationDialog() {
        var dialogCustomBox=AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.examination_custom_layout, null)
        dialogCustomBox.setView(dialogView).show()
    }

    private fun showMedicamentDialog() {
        var dialogCustomBox=AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.medicament_custom_layout, null)
        dialogCustomBox.setView(dialogView).show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.add(0, v.id, 0, "Edit");
        menu.add(0, v.id, 0, "Delete");
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.title == "Edit") Toast.makeText(applicationContext, "Edit Clicked", Toast.LENGTH_LONG).show();
        if (item.title == "Delete") {
            val dialogBox = AlertDialog.Builder(this).create()
            dialogBox.setTitle("Are you sure you want to delete this event?");
            dialogBox.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", { _, i ->
                Toast.makeText(applicationContext, "Yes", Toast.LENGTH_LONG).show()

            })
            dialogBox.setButton(AlertDialog.BUTTON_NEUTRAL, "No", { _, i ->
                Toast.makeText(applicationContext, "No", Toast.LENGTH_LONG).show()

            })
            dialogBox.show()
        }
        return true;
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