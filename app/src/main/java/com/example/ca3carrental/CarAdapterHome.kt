package com.example.ca3carrental

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.get

class CarAdapterHome(private val context: Context, private val carList: List<Car>) : RecyclerView.Adapter<CarAdapterHome.CarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val currentCar = carList[position]
        holder.bind(currentCar)
    }

    override fun getItemCount() = carList.size

    inner class CarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val carImageView: ImageView = itemView.findViewById(R.id.vehicleImage)
        private val carNameTextView: TextView = itemView.findViewById(R.id.vehicle)
        private val priceTextView: TextView = itemView.findViewById(R.id.price)

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

        fun bind(car: Car) {
            carNameTextView.text = car.carName
            priceTextView.text = "\u20B9${car.price}"
//            Toast.makeText(context,"${car.imageUrl}",Toast.LENGTH_SHORT).show()
            Glide.with(context)
                .load(car.imageUrl)
                .into(carImageView)
//            Glide.with(context).load(car.imageUrl.toUri())
//                .apply(
//                    RequestOptions()
//                        .error(R.drawable.user)
//                        .placeholder(R.drawable.user)
//                        .diskCacheStrategy(DiskCacheStrategy.ALL)
//                ).into(carImageView)
        }
    }
}