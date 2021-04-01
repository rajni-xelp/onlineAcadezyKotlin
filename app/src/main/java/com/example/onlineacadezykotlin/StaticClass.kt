package com.example.onlineacadezykotlin

import android.graphics.Bitmap
import android.net.Uri

class StaticClass {
    companion object
    {
      var draw:Boolean=false
        var matchParentHeight:Int=0
        var selectedListOfImages=ArrayList<String>()
        lateinit var zoomedImage:Uri
        var zoomed_position=-1
        lateinit var selectedImage:Bitmap

    }
}