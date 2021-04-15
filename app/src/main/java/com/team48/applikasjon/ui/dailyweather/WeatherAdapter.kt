package com.team48.applikasjon.ui.dailyweather
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Weather

class WeatherAdapter(
    private var weatherList: MutableList<Weather>

    ) : RecyclerView.Adapter<WeatherAdapter.DataViewHolder>() {


    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.textViewLocation)
        val temperature : TextView = itemView.findViewById(R.id.textViewTemperature)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : DataViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DataViewHolder(inflater.inflate(R.layout.weatheritem_layout, parent, false))
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.location.text = weatherList[position].location
        holder.temperature.text = weatherList[position].temperature.toString()
    }

    override fun getItemCount(): Int = weatherList.size

    fun addData(list: List<Weather>) {
        weatherList.addAll(list)
    }

}