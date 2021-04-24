package com.team48.applikasjon.ui.map.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.team48.applikasjon.R

class SpinnerAdapter(
        context: Context,
        val icons: MutableList<Int>,
) : ArrayAdapter<Int>(context, 0, icons) {


    override fun getCount(): Int {
        return icons.size
    }

    /* Returnerer valgt spinner view */
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    /* Returnerer dropdown view på position */
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    /* Custom metode for å inflate views med riktig ikon iht posisjon */
    private fun initView(position: Int, convertView: View?, parent: ViewGroup) : View {
        var view = convertView // Må gjøre om til var for å manipulere viewet
        if (view == null) {
            view = LayoutInflater.from(context).inflate(
                    R.layout.spinner_item, parent, false
            )
        }
        val iconView: ImageView? = view?.findViewById(R.id.weather_icon)
        iconView?.setImageResource(icons[position])
        return view!!
    }
}