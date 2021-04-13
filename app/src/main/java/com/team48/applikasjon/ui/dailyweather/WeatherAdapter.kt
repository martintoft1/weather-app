package com.team48.applikasjon.ui.dailyweather
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.Weather

class MainAdapter(
    private val users: ArrayList<Weather>
) : RecyclerView.Adapter<MainAdapter.DataViewHolder>() {

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(weather: Weather) {
            val location: TextView = itemView.findViewById(R.id.textViewLocation)
            val temperature : TextView = itemView.findViewById(R.id.textViewTemperature)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.weatheritem_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = users.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(users[position])

    fun addData(list: List<Weather>) {
        users.addAll(list)
    }

}