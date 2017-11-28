package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event

/**
 * Created by aleksander on 19.11.17.
 */
interface EventListMVP {

    interface Model {
        fun getAllEvents(): MutableList<Event>
        fun saveEvent(event: Event)
        fun updateEvent(idToUpdate: Long, newEvent: Event)
        fun deleteEvent(id: Long)

    }

    interface Presenter {
        fun getEvents()
        fun saveEvent(event: Event)
        fun updateEvent(idToUpdate: Long, newEvent: Event)
        fun deleteEvent(id: Long)
    }


    interface View {
        fun showEvents(events: MutableList<Event>)
        fun showInfo(message: String)
    }
}
