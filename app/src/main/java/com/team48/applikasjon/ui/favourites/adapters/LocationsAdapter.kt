package com.team48.applikasjon.ui.favourites.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Location

class LocationsAdapter(
    private var locations: List<Location>,
    private var listener: OnLocationClickListener

    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {


    override fun getItemCount(): Int = locations.size


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.locationitem_layout,
            parent, false), listener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: Location = locations[position]
        holder.name.text = location.name

        // Setter ikoner basert på værdata
        location.cloud_percentage?.let { holder.iv_cloud.setImageLevel(it.toInt()) }
        location.rain_mm?.let          { holder.iv_rain.setImageLevel(it.toInt()) }
        location.temp_celsius?.let     { holder.iv_temp.setImageLevel(it.toInt()) }

    }


    class ViewHolder(val view: View, val listener: OnLocationClickListener)
        : RecyclerView.ViewHolder(view), View.OnClickListener {

        val name:     TextView  = view.findViewById(R.id.tv_location)   // Location name
        val iv_cloud: ImageView = view.findViewById(R.id.iv_cloud)      // Cloud icon
        val iv_rain:  ImageView = view.findViewById(R.id.iv_rain)       // Rain icon
        val iv_temp:  ImageView = view.findViewById(R.id.iv_temp)       // Temperature icon

        val ib_map:   ImageButton = view.findViewById(R.id.go_to_map)   // Navigate to map button
        val ib_del:   ImageButton = view.findViewById(R.id.delete)      // Delete location button

        init {
            view.setOnClickListener(this)
            ib_map.setOnClickListener(this)
            ib_del.setOnClickListener(this)
        }


        override fun onClick(v: View?) {
            when (v?.id) {
                view.id   -> listener.onLocationClick(bindingAdapterPosition, view)
                ib_map.id -> listener.onLocationMapClick(bindingAdapterPosition)
                ib_del.id -> listener.onLocationDeleteClick(bindingAdapterPosition)
            }
        }
    }


    interface OnLocationClickListener {
        fun onLocationClick(position: Int, view: View)
        fun onLocationDeleteClick(position: Int)
        fun onLocationMapClick(position: Int)
    }
}