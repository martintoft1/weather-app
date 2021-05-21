package com.team48.applikasjon.ui.map.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.team48.applikasjon.R

/* Adapter for spinneren for visning av værtyper i kartfragment */
class SpinnerAdapter(
        context: Context,
        val icons: MutableList<Int>,
) : ArrayAdapter<Int>(context, 0, icons) {

    // Henter antall gjenstander i spinner
    override fun getCount(): Int {
        return icons.size
    }

    /* Returnerer valgt spinner view */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView // Må gjøre om til var for å manipulere viewet
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.spinner_selected_item, parent, false
            )
        }
        val iconView: ImageView? = view?.findViewById(R.id.weather_icon)
        iconView?.setImageResource(icons[position])
        return view!!
    }

    /* Returnerer dropdown view på position */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    /* Justert metode for å inflate views med riktig ikon iht. posisjon */
    private fun initView(position: Int, convertView: View?, parent: ViewGroup) : View {
        var view = convertView // Må gjøre om til var for å manipulere viewet
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.spinner_dropdown_item, parent, false
            )
        }

        // Legge til tilhørende ikon
        val iconView: ImageView? = view?.findViewById(R.id.weather_icon)
        iconView?.setImageResource(icons[position])

        // Legge til tilhørende etikett
        val contentDescriptions: Array<out String> = view!!.resources.getStringArray(R.array.descriptions_array)
        view.contentDescription = contentDescriptions[position]

        return view
    }
}