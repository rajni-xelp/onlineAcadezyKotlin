package com.example.onlineacadezykotlin

import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ChooseImageToZoom : Fragment() {
  lateinit var imageUri:String
    lateinit var navController: NavController
    lateinit var mLongPressed: Runnable
    var handler: Handler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageUri = it.getString("imageUri")!!
        }
        handler = Handler()
    }

    lateinit var bmp : Bitmap
    lateinit var selected_image_paintview:PaintView
    lateinit var rl_pencil:RelativeLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_choose_image_to_zoom, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
        val bt_fetchbitmap=view.findViewById<Button>(R.id.bt_fetchbitmap)
         rl_pencil=view.findViewById<RelativeLayout>(R.id.rl_pencil)
        selected_image_paintview=view.findViewById<PaintView>(R.id.pv_zoomed)
        bt_fetchbitmap.setOnClickListener {
            selected_image_paintview.setDrawingCacheEnabled(true)
            val bit :Bitmap= selected_image_paintview.getDrawingCache()

            StaticClass.zoomedImage= getImageUri(bit)!!
            navController.previousBackStackEntry?.savedStateHandle?.set("key", "zoomed_image")
            navController.navigateUp()
        }

        rl_pencil.setOnClickListener {
            if (StaticClass.draw) {
                StaticClass.draw = false
                rl_pencil.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.light_grey))
            } else {
                StaticClass.draw = true
                rl_pencil.backgroundTintList =
                    ColorStateList.valueOf(resources.getColor(R.color.colorPrimary))
            }
        }


        mLongPressed = Runnable {
            callRestOfImage()
        }
        handler!!.postDelayed(mLongPressed, 500)
    }

    fun callRestOfImage()
    {
        val mUri=Uri.parse(imageUri)
        try {
            bmp = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), mUri)
            val file = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "share_image_" + System.currentTimeMillis() + ".jpeg"
            )
            val out = FileOutputStream(file)
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close()

        } catch (e: Exception) {

        }
        selected_image_paintview.putImage(bmp)
    }

    fun getImageUri(bmp: Bitmap): Uri? {
        var bmpUri: Uri? = null
        try {
            val file: File = File(
                requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "IMG_" + System.currentTimeMillis() + ".jpg"
            )
            val out = FileOutputStream(file)
           bmp.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.close()
            bmpUri = Uri.fromFile(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bmpUri
    }

    override fun onPause() {
        super.onPause()
        StaticClass.draw=false
    }

    override fun onStop() {
        super.onStop()
        if(handler !=null)
        handler!!.removeCallbacks(mLongPressed)
    }
}