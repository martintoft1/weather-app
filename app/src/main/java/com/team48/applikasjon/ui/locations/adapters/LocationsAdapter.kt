package com.team48.applikasjon.ui.locations.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.LocationModel

/* Adapter for håndtering av lokasjoner opp i mot databasen */
class LocationsAdapter(

    // Lokal liste over favoritt-lokasjoner og en clickListener for favoritt-knappen
    private var locationModels: MutableList<LocationModel>,
    private var clickListener: OnLocationClickListener

    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {

    override fun getItemCount(): Int = locationModels.size

    // Legger inn en ny lokasjon i den listen
    fun setLocations(locationModels: MutableList<LocationModel>) {
        this.locationModels = locationModels
        notifyDataSetChanged()
    }

    // Standardfunksjon relatert til RecycleView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.locationitem_layout,
            parent, false), clickListener)
    }

    // Standardfunksjon relatert til RecycleView
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val locationModel: LocationModel = locationModels[position]
        holder.name.text = locationModel.name

        // Setter ikoner basert på værdata
        locationModel.cloud_percentage?.let { holder.iv_cloud.setImageLevel(it.toInt()) }
        locationModel.rain_mm?.let          { holder.iv_rain.setImageLevel(it.toInt()) }
        locationModel.temp_celsius?.let     { holder.iv_temp.setImageLevel(it.toInt()) }

        // Setter værdata
        val cloudString = holder.itemView.context.getString(R.string.clouds_desc)
        val rainString  = holder.itemView.context.getString(R.string.rain_desc)
        val tempString  = holder.itemView.context.getString(R.string.temp_desc)

        holder.tv_cloud.text = String.format(cloudString, locationModel.cloud_percentage)
        holder.tv_rain.text  = String.format(rainString, locationModel.rain_mm)
        holder.tv_temp.text  = String.format(tempString, locationModel.temp_celsius)
    }

    /* Indre klasse for RecyclerView */
    class ViewHolder(
            val view: View,
            val clickListener: OnLocationClickListener
        ) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val swipeableContent = view.findViewById<LinearLayout>(R.id.swipe_container)

        val name:     TextView  = view.findViewById(R.id.tv_location)   // Location name
        val iv_cloud: ImageView = view.findViewById(R.id.iv_cloud)      // Cloud icon
        val iv_rain:  ImageView = view.findViewById(R.id.iv_rain)       // Rain icon
        val iv_temp:  ImageView = view.findViewById(R.id.iv_temp)       // Temperature icon

        val tv_cloud: TextView  = view.findViewById(R.id.tv_clouds)     // Clouds (%)
        val tv_rain:  TextView  = view.findViewById(R.id.tv_rain)       // Rain   (mm)
        val tv_temp:  TextView  = view.findViewById(R.id.tv_temp)       // Temp   (°C)

        init { view.setOnClickListener(this) }

        override fun onClick(v: View) {
            when (v.id) {
                view.id   -> clickListener.onLocationClick(bindingAdapterPosition, view)
            }
        }
    }

    // Grensesnitt for trykking på favorittknapp
    interface OnLocationClickListener {
        fun onLocationClick(position: Int, view: View)
    }
}