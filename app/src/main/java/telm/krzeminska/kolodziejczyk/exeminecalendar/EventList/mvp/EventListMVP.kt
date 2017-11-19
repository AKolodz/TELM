package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event

/**
 * Created by aleksander on 19.11.17.
 */
interface EventListMVP {

    interface EventListModel {
        fun getEvents(): MutableList<Event>
    }

    interface EventListPresenter {
        fun getEvents(): Unit
    }

    interface EventListView {
        fun listEvents(events: MutableList<Event>): Unit
    }
}
