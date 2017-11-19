package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

/**
 * Created by aleksander on 19.11.17.
 */
interface EventListMVP {

    interface EventListModel {
        fun getEvents() : MutableList<Event>
    }

    interface EventListPresenter {

    }

    interface EventListView {

    }
}
