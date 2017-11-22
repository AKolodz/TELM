package telm.krzeminska.kolodziejczyk.exeminecalendar.EventList

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp.EventListMVP
import telm.krzeminska.kolodziejczyk.exeminecalendar.EventList.mvp.EventListPresenter
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event

class MainActivity : AppCompatActivity(), EventListMVP.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val presenter = EventListPresenter(this)
        presenter.getEvents()

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun showEvents(events: MutableList<Event>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
