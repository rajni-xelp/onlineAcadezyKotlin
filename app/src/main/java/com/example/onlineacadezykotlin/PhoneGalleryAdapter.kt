package com.example.onlineacadezykotlin

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        val view=LayoutInflater.from(context).inflate(R.layout.phone_gallery_adapter,parent,false)
        val myHandler=Myhandler(view)
        return myHandler
    }

    override fun getItemCount(): Int {
        return imagesGallery.size
    }
    override fun onBindViewHolder(holder: Myhandler, position: Int) {

        holder.camera_image.visibility=View.GONE
        holder.bg_img.visibility=View.VISIBLE
        Log.d("xskxkjsakcj",position.toString()+"  , "+imagesGallery.get(position).getImage())
        Glide.with(context).load(imagesGallery.get(position).getImage()).into(holder.img_galley)

        holder.bg_img.isSelected=false
        holder.circle_img.visibility=View.GONE
        holder.img_galley_border.visibility=View.GONE

        if(imagesGallery.get(position).isSelected())
        {
            holder.img_galley_border.visibility=View.VISIBLE
            holder.circle_img.visibility=View.VISIBLE
        }
        else
        {
            holder.img_galley_border.visibility=View.GONE
            holder.circle_img.visibility=View.GONE
        }

        holder.bg_img.setOnClickListener {
            //upload images
            if(imagesGallery.get(position).isSelected)
            {
                imagesGallery.get(position).isSelected=false
                holder.img_galley_border.visibility=View.GONE
                holder.circle_img.visibility=View.GONE
                maxNoOfImages--
                counter--
                galleryImagesList.remove(imagesGallery.get(position).image)
            }
            else
            {
                if(maxNoOfImages<1)
                {
                    imagesGallery.get(position).isSelected=true
                    holder.img_galley_border.visibility=View.VISIBLE
                    holder.circle_img.visibility=View.VISIBLE
                    counter++
                    maxNoOfImages++
                    galleryImagesList.add(imagesGallery.get(position).image)
                }
                else
                {
                    Toast.makeText(context,"You can only select 1 image",Toast.LENGTH_SHORT).show()
                }
            }
            if(galleryImagesList.isNotEmpty())
            {
                showGalleryNextButtonInterface?.showGalleryNextButton(maxNoOfImages,galleryImagesList.get(0))
            }
            else
            {
                showGalleryNextButtonInterface?.showGalleryNextButton(maxNoOfImages,"")
            }
        }
    }

    class Myhandler(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val img_galley=itemView.findViewById<AppCompatImageView>(R.id.img_galley)
        val img_galley_border=itemView.findViewById<AppCompatImageView>(R.id.img_galley_border)
        val circle_img=itemView.findViewById<AppCompatImageView>(R.id.circle_img)
        val bg_img=itemView.findViewById<FrameLayout>(R.id.bg_img)
        val camera_image=itemView.findViewById<FrameLayout>(R.id.camera_image)
    }

   interface ShowGalleryNextButtonInterface
   {
      fun showGalleryNextButton( gallerySelectedContentSize:Int, galleryImage:String)
   }

}