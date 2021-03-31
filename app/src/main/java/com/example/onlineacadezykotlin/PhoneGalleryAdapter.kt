package com.example.onlineacadezykotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineacadezykotlin.databinding.PhoneGalleryAdapterBinding
import java.util.ArrayList

class PhoneGalleryAdapter(val context :Context, phoneGalleryFragment: PhoneGalleryFragment,val imagesGallery : ArrayList<GalleryModel>) : RecyclerView.Adapter<PhoneGalleryAdapter.Myhandler>() {
    var maxNoOfImages=0
    var counter=0
    var galleryImagesList:ArrayList<String>
    var showGalleryNextButtonInterface : ShowGalleryNextButtonInterface
    init {
        galleryImagesList= ArrayList()
        showGalleryNextButtonInterface = phoneGalleryFragment
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Myhandler {
        val phoneGalleryAdapterBinding=PhoneGalleryAdapterBinding.inflate(LayoutInflater.from(context),parent,false)
        return Myhandler(phoneGalleryAdapterBinding)
    }

    override fun getItemCount(): Int {
        return imagesGallery.size
    }
    override fun onBindViewHolder(holder: Myhandler, position: Int) {

        holder.bind(imagesGallery.get(position))
        holder.bg_img.visibility=View.VISIBLE

        holder.bg_img.isSelected=false
        holder.circle_img.visibility=View.GONE

        if(imagesGallery.get(position).isSelected)
        {
            holder.circle_img.visibility=View.VISIBLE
        }
        else
        {
            holder.circle_img.visibility=View.GONE
        }

        holder.bg_img.setOnClickListener {
            //upload images
            if(imagesGallery.get(position).isSelected)
            {
                imagesGallery.get(position).isSelected=false
                holder.circle_img.visibility=View.GONE
                maxNoOfImages--
                counter--
                galleryImagesList.remove(imagesGallery.get(position).image)
            }
            else
            {
                if(maxNoOfImages<3)
                {
                    imagesGallery.get(position).isSelected=true
                    holder.circle_img.visibility=View.VISIBLE
                    counter++
                    maxNoOfImages++
                    imagesGallery.get(position).image?.let { it1 -> galleryImagesList.add(it1) }
                }
                else
                {
                    Toast.makeText(context,"You can only select 3 images at once",Toast.LENGTH_SHORT).show()
                }
            }
            showGalleryNextButtonInterface?.showGalleryNextButton(maxNoOfImages,galleryImagesList)
//            if(galleryImagesList.isNotEmpty())
//            {
//                showGalleryNextButtonInterface?.showGalleryNextButton(maxNoOfImages,galleryImagesList)
//            }
//            else
//            {
//                showGalleryNextButtonInterface?.showGalleryNextButton(maxNoOfImages,galleryImagesList)
//            }
        }
    }

    class Myhandler(val binding: PhoneGalleryAdapterBinding) : RecyclerView.ViewHolder(binding.root)
    {
        val circle_img=binding.root.findViewById<AppCompatImageView>(R.id.circle_img)
        val bg_img=binding.root.findViewById<FrameLayout>(R.id.bg_img)

        fun bind(galleryModel: GalleryModel)
        {
            binding.galleryModel=galleryModel
            binding.setVariable(BR.gallery_model,galleryModel)
            binding.executePendingBindings()
        }
    }

   interface ShowGalleryNextButtonInterface
   {
      fun showGalleryNextButton( gallerySelectedContentSize:Int, galleryImageList:List<String>)
   }

}