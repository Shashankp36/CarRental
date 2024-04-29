package com.example.ca3carrental

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class CarAdapter(private val context: Context, private val carList: List<Car>) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewCar: ImageView = itemView.findViewById(R.id.vehicleImage)
        val textViewName: TextView = itemView.findViewById(R.id.vehicle)
        val textViewPrice: TextView = itemView.findViewById(R.id.price)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val intent = Intent(context, CarDetails::class.java).apply {
                        putExtra("CAR_ID", carList[position].carId)
                    }
                    context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_car, parent, false)
        return CarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentItem = carList[position]

        // Load image from imageUrl using Glide
        Glide.with(holder.itemView)
            .load(currentItem.imageUrl)
            // Optional placeholder image while loading
            // Optional error image if loading fails
            .into(holder.imageViewCar)

        holder.textViewName.text = currentItem.carName
        holder.textViewPrice.text = currentItem.price.toString()
    }

    override fun getItemCount() = carList.size
}
