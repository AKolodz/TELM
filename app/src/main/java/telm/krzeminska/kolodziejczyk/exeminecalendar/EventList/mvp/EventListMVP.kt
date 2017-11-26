package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event

/**
 * Created by aleksander on 19.11.17.
 */
interface EventListMVP {

    interface Model {
        fun getAllEvents(): MutableList<Event>
    }

    interface Presenter {
        fun getEvents(): Unit
    }

    interface View {
        fun showEvents(events: MutableList<Event>): Unit
    }
}
