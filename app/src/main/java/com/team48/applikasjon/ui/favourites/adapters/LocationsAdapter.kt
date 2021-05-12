package com.team48.applikasjon.ui.favourites.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Location
import com.team48.applikasjon.utils.LocationSwipeHandler

class LocationsAdapter(
    private var locations: MutableList<Location>,
    private var clickListener: OnLocationClickListener

    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {


    override fun getItemCount(): Int = locations.size


    fun setLocations(locations: MutableList<Location>) {
        this.locations = locations
        notifyDataSetChanged()
    }

    fun removeLocation(position: Int) {
        locations.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.locationitem_layout,
            parent, false), clickListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location: Location = locations[position]
        holder.name.text = location.name

        // Setter ikoner basert på værdata
        location.cloud_percentage?.let { holder.iv_cloud.setImageLevel(it.toInt()) }
        location.rain_mm?.let          { holder.iv_rain.setImageLevel(it.toInt()) }
        location.temp_celsius?.let     { holder.iv_temp.setImageLevel(it.toInt()) }

        // Setter værdata
        holder.tv_cloud.text = location.cloud_percentage.toString()
        holder.tv_rain.text  = location.rain_mm.toString()
        holder.tv_temp.text  = location.temp_celsius.toString()
    }


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

    interface OnLocationClickListener {
        fun onLocationClick(position: Int, view: View)
    }
}