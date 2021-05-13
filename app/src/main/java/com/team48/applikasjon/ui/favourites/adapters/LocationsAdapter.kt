package com.team48.applikasjon.ui.favourites.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.DatabaseLocation

class LocationsAdapter(
    private var databaseLocations: MutableList<DatabaseLocation>,
    private var clickListener: OnLocationClickListener

    ) : RecyclerView.Adapter<LocationsAdapter.ViewHolder>() {


    override fun getItemCount(): Int = databaseLocations.size


    fun setLocations(databaseLocations: MutableList<DatabaseLocation>) {
        this.databaseLocations = databaseLocations
        notifyDataSetChanged()
    }

    fun removeLocation(position: Int) {
        databaseLocations.removeAt(position)
        notifyItemRemoved(position)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.locationitem_layout,
            parent, false), clickListener)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val databaseLocation: DatabaseLocation = databaseLocations[position]
        holder.name.text = databaseLocation.name

        // Setter ikoner basert på værdata
        databaseLocation.cloud_percentage?.let { holder.iv_cloud.setImageLevel(it.toInt()) }
        databaseLocation.rain_mm?.let          { holder.iv_rain.setImageLevel(it.toInt()) }
        databaseLocation.temp_celsius?.let     { holder.iv_temp.setImageLevel(it.toInt()) }

        // Setter værdata
        val cloudString = holder.itemView.context.getString(R.string.clouds_desc)
        val rainString  = holder.itemView.context.getString(R.string.rain_desc)
        val tempString  = holder.itemView.context.getString(R.string.temp_desc)

        holder.tv_cloud.text = String.format(cloudString, databaseLocation.cloud_percentage)
        holder.tv_rain.text  = String.format(rainString, databaseLocation.rain_mm)
        holder.tv_temp.text  = String.format(tempString, databaseLocation.temp_celsius)
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