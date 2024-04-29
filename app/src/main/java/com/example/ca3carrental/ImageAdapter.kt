package com.example.ca3carrental

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2

class ImageAdapter(private val images: List<Int>) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_view, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val imageResId = images[position]
        holder.imageView.setImageResource(imageResId)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}

class HorizontalPeekPageTransformer : ViewPager2.PageTransformer {
    private val horizontalMargin = -0.2f // Adjust this value for the peek effect

    override fun transformPage(page: View, position: Float) {
        when {
            position < -1 -> page.alpha = 0f
            position <= 1 -> {
                page.alpha = 1f
                page.translationX = -page.width * horizontalMargin * position
                val scaleFactor = 1 - (horizontalMargin * Math.abs(position))
                page.scaleX = scaleFactor
                page.scaleY = scaleFactor
            }
            else -> page.alpha = 0f
        }
    }
}


