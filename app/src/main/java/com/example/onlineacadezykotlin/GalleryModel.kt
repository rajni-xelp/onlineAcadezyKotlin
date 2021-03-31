package com.example.onlineacadezykotlin
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

class GalleryModel {
    var image: String? = null
    var isSelected = false
    var imageName: String? = null

}

@BindingAdapter(value = ["imageUrl"], requireAll = false)
fun loadImage(imageview:AppCompatImageView,imageUrl:String)
{
    Glide.with(imageview.context).load(imageUrl).into(imageview)
}