package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.CalendarContract
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.examination_custom_layout.view.*
import kotlinx.android.synthetic.main.medicament_custom_layout.view.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListMVP
import telm.krzeminska.kolodziejczyk.exeminecalendar.event_list.mvp.EventListPresenter
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.EventType
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.ReminderType
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.TimeToEvent
import java.time.LocalDateTime


class MainActivity : AppCompatActivity(), EventListMVP.View {

    private val CALENDAR_PERMISSION_REQUESTCODE = 1
    private var events = mutableListOf<Event>()
    private lateinit var eventListView: ListView
    private lateinit var presenter: EventListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED) {
            initViews()
            pullEventsList()
        } else
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR),
                    CALENDAR_PERMISSION_REQUESTCODE)

    }

    private fun initViews() {
        eventListView = eventList
        val adapter = EventAdapter(this, R.layout.rowlayout, events)
        eventListView.adapter = adapter
        eventListView.setOnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this, "Item $i", Toast.LENGTH_SHORT).show()
        }

        registerForContextMenu(eventListView)

        show_calendar_button.setOnClickListener {
            showInCalendar()
        }

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
        val dialogCustomBox = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.examination_custom_layout, null)

        dialogCustomBox.setPositiveButton("Save", { dialogInterface, i ->
            val name = dialogView.examination_name_et.text.toString()
            val description = dialogView.examination_place_et.text.toString()
            val dateTime: LocalDateTime =
                    if (dialogView.examination_year_et.text.toString().isEmpty() ||
                            dialogView.examination_month_et.text.toString().isEmpty() ||
                            dialogView.examination_day_et.text.toString().isEmpty() ||
                            dialogView.examination_time_et.text.toString().isEmpty()) {
                        LocalDateTime.now().plusDays(1)
                    } else {
                        LocalDateTime.of(
                                dialogView.examination_year_et.text.toString().toInt(),
                                dialogView.examination_month_et.text.toString().toInt(),
                                dialogView.examination_day_et.text.toString().toInt(),
                                dialogView.examination_time_et.text.toString().substringBefore(":").toInt(),
                                dialogView.examination_time_et.text.toString().substringAfter(":").toInt())
                    }
            val days: Int = dialogView.examination_days_before_et.text.toString().let {
                if (it.isEmpty())
                    0
                else
                    it.toInt()
            }
            val hours = dialogView.examination_hours_before_et.text.toString().let {
                if (it.isEmpty())
                    0
                else
                    it.toInt()
            }
            val minutes = dialogView.examination_minutes_before_et.text.toString().let {
                if (it.isEmpty())
                    0
                else
                    it.toInt()
            }
            val timeToEvent = TimeToEvent(days, hours, minutes)
            val reminder =
                    when {
                        dialogView.examination_alarm_cb.isChecked -> Pair(ReminderType.ALARM, timeToEvent)
                        dialogView.examination_email_cb.isChecked -> Pair(ReminderType.MAIL, timeToEvent)
                        dialogView.examination_sms_cb.isChecked -> Pair(ReminderType.SMS, timeToEvent)
                        else -> Pair(ReminderType.NONE, timeToEvent)
                    }
            val eventToSave = Event(null, name, description, dateTime, null, reminder, EventType.EXAMINATION)
            presenter.saveEvent(eventToSave)
            presenter.getEvents()
            dialogInterface.dismiss()
        })
        dialogCustomBox.setNegativeButton("Cancel", { dialogInterface, i ->
            dialogInterface.dismiss()
        })
        dialogCustomBox.setView(dialogView).show()
    }

    private fun showMedicamentDialog() {
        val dialogCustomBox = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.medicament_custom_layout, null)

        dialogCustomBox.setPositiveButton("Save", { dialogInterface, i ->
            val name = dialogView.medicament_name_et.text.toString()
            val description = dialogView.medicament_dose_et.text.toString()
            val dateTime: LocalDateTime =
                    if (dialogView.medicament_year_et.text.toString().isEmpty() ||
                            dialogView.medicament_month_et.text.toString().isEmpty() ||
                            dialogView.medicament_day_et.text.toString().isEmpty() ||
                            dialogView.medicament_time_et.text.toString().isEmpty()) {
                        LocalDateTime.now().plusDays(1)
                    } else {
                        LocalDateTime.of(
                                dialogView.medicament_year_et.text.toString().toInt(),
                                dialogView.medicament_month_et.text.toString().toInt(),
                                dialogView.medicament_day_et.text.toString().toInt(),
                                dialogView.medicament_time_et.text.toString().substringBefore(":").toInt(),
                                dialogView.medicament_time_et.text.toString().substringAfter(":").toInt())
                    }
            val hours: Int = dialogView.medicament_hours_before_et.text.toString().let {
                if (it.isEmpty())
                    0
                else
                    it.toInt()
            }
            val minutes = dialogView.medicament_minutes_before_et.text.toString().let {
                if (it.isEmpty())
                    0
                else
                    it.toInt()
            }
            val timeToEvent = TimeToEvent(0, hours, minutes)
            val reminder =
                    when {
                        dialogView.medicament_alarm_cb.isChecked -> Pair(ReminderType.ALARM, timeToEvent)
                        dialogView.medicament_email_cb.isChecked -> Pair(ReminderType.MAIL, timeToEvent)
                        dialogView.medicament_sms_cb.isChecked -> Pair(ReminderType.SMS, timeToEvent)
                        else -> Pair(ReminderType.NONE, timeToEvent)
                    }
            val durationDays = dialogView.medicament_duration_et.text.toString().let {
                if (it.isEmpty())
                    1
                else
                    it.toInt()
            }
            val eventToSave = Event(null, name, description, dateTime, durationDays, reminder, EventType.MEDICAMENT)
            presenter.saveEvent(eventToSave)
            presenter.getEvents()
            dialogInterface.dismiss()
        })
        dialogCustomBox.setNegativeButton("Cancel", { dialogInterface, i -> dialogInterface.dismiss() })
        dialogCustomBox.setView(dialogView).show()
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo) {
        menu.add(0, v.id, 0, "Show in calendar");
        menu.add(0, v.id, 0, "Delete");
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info: AdapterView.AdapterContextMenuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val id = events[info.position].id
        if (item.title == "Show in calendar")
            showEventInCalendar(id ?: throw IllegalArgumentException("Event ID can't be null"))
        else if (item.title == "Delete") {
            val dialogBox = AlertDialog.Builder(this).create()
            dialogBox.setTitle("Are you sure you want to delete this event?")
            dialogBox.setButton(AlertDialog.BUTTON_POSITIVE, "Yes", { _, i ->
                presenter.deleteEvent(id ?: throw IllegalArgumentException("Event ID can't be null"))
                        .apply { presenter.getEvents() }
            })
            dialogBox.setButton(AlertDialog.BUTTON_NEUTRAL, "No", { _, i ->
                Toast.makeText(applicationContext, "No", Toast.LENGTH_LONG).show()

            })
            dialogBox.show()
        }
        return true
    }

    private fun showEventInCalendar(eventId: Long): Unit =
            ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId)
                    .run {
                        Intent(Intent.ACTION_VIEW).setData(this)
                                .run { startActivity(this) }
                    }

    private fun showInCalendar(): Unit =
            CalendarContract.CONTENT_URI.buildUpon()
                    .appendPath("time")
                    .apply { ContentUris.appendId(this, 0) }
                    .run { Intent(Intent.ACTION_VIEW).setData(this.build()) }
                    .run { startActivity(this) }

    private fun pullEventsList() {
        presenter = EventListPresenter(this, applicationContext)
        presenter.getEvents()
    }

    override fun showInfo(message: String) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun showEvents(dbEvents: MutableList<Event>) {
        events = dbEvents
        (eventListView.adapter as EventAdapter).apply {
            this.clear()
            this.addAll(events)
        }
        ((eventListView.adapter) as BaseAdapter).notifyDataSetChanged()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CALENDAR_PERMISSION_REQUESTCODE ->
                if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission must be granted", Toast.LENGTH_SHORT).show()
                    this.finish()
                } else {
                    initViews()
                    pullEventsList()
                }

        }
    }
}
