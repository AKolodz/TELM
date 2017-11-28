package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.content.Context
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event

/**
 * Created by aleksander on 22.11.17.
 */
class EventListPresenter(private val view: EventListMVP.View, applicationContext: Context)
    : EventListMVP.Presenter {
    private val model = EventListModel(applicationContext)

    override fun saveEvent(event: Event) {
        model
                .saveEvent(event)
                .apply {
                    view.showInfo("Event saved")
                }
    }

    override fun updateEvent(idToUpdate: Long, newEvent: Event) {
        model
                .updateEvent(idToUpdate, newEvent)
                .apply {
                    view.showInfo("Event updated")
                }
    }

    override fun deleteEvent(id: Long) {
        model
                .deleteEvent(id)
                .apply {
                    view.showInfo("Event deleted")
                }
    }

    override fun getEvents() {
        model
                .getAllEvents()
                .apply {
                    view.showEvents(this)
                }
    }
}