package telm.krzeminska.kolodziejczyk.exeminecalendar.event_list

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.rowlayout.view.*
import telm.krzeminska.kolodziejczyk.exeminecalendar.R
import telm.krzeminska.kolodziejczyk.exeminecalendar.model.Event


/**
 * Created by Acer on 17.12.2017.
 */
class EventAdapter(context: Context?, var resource: Int, private var objects: MutableList<Event>?)
    : ArrayAdapter<Event>(context, resource, objects) {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(R.layout.rowlayout, parent, false)

        val name = row.name
        val description = row.description
        name.text = objects?.get(position)?.name
        description.text = objects?.get(position)?.description

        return row
    }
}