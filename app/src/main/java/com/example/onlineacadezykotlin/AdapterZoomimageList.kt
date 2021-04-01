package com.example.onlineacadezykotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.onlineacadezykotlin.databinding.AdapterZoominBinding
import com.example.onlineacadezykotlin.databinding.PhoneGalleryAdapterBinding

class AdapterZoomimageList(
    val context: Context,val imageList :List<String>
,val sendPositionInterface : SendPositionInterface) : RecyclerView.Adapter<AdapterZoomimageList.Myhandler>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):Myhandler{
        val adapterZoominBinding=AdapterZoominBinding.inflate(LayoutInflater.from(context), parent, false)
        return Myhandler(adapterZoominBinding)
    }

    override fun onBindViewHolder(holder: Myhandler, position: Int) {
        Glide.with(context).load(imageList.get(position)).into(holder.img_galley)
        holder.img_galley.setOnClickListener {
            sendPositionInterface.sendPosition(position)
        }
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    class Myhandler(val binding: AdapterZoominBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val img_galley = binding.root.findViewById<AppCompatImageView>(R.id.img_galley)
    }

    interface SendPositionInterface
    {
        fun sendPosition(position: Int)
    }
}