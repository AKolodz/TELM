package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

import android.content.Context

/**
 * Created by aleksander on 22.11.17.
 */
class EventListPresenter(private val view: EventListMVP.View, applicationContext: Context)
    : EventListMVP.Presenter {

    private val model = EventListModel(applicationContext)

    override fun getEvents() {
        model
                .getEvents()
                .apply {
                    view.showEvents(this)
                }
    }
}