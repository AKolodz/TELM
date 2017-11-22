package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp

/**
 * Created by aleksander on 22.11.17.
 */
class EventListPresenter(private val view: EventListMVP.View) : EventListMVP.Presenter {
    private val model = EventListModel()

    override fun getEvents() {
        model
                .getEvents()
                .apply {
                    view.showEvents(this)
                }
    }
}