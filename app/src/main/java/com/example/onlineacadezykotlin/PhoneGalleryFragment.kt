package com.example.onlineacadezykotlin

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class PhoneGalleryFragment : Fragment() , PhoneGalleryAdapter.ShowGalleryNextButtonInterface {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var phoneGalleryAdapter:PhoneGalleryAdapter;
    lateinit var recyclerView:RecyclerView
    lateinit var listOfAllImages:ArrayList<GalleryModel>
    lateinit var  progress_bar : ProgressBar
    lateinit var textView_roboto_boldNext:TextView
    lateinit var galleryImageNew:String
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_phone_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progress_bar=view.findViewById(R.id.progress_bar)
        textView_roboto_boldNext = view.findViewById(R.id.next_text);
        progress_bar.setVisibility(View.GONE)
        textView_roboto_boldNext.setEnabled(true)
        recyclerView = view.findViewById(R.id.recyclerView)
        navController= Navigation.findNavController(view)

       recyclerView.setHasFixedSize(true);
        val gridLayoutManager =  GridLayoutManager(requireActivity(), 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        recyclerView.setLayoutManager(gridLayoutManager)
        getGalleryData()

        textView_roboto_boldNext.setOnClickListener {
            navController.previousBackStackEntry?.savedStateHandle?.set("key", galleryImageNew)
            navController.navigateUp()
        }
    }

    fun getGalleryData()
    {
        try {
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection =
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val cursor: Cursor? = requireActivity().getContentResolver()
                .query(uri, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC")
            val column_index_data: Int =
                cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            val column_index_folder_name: Int = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            var absolutePathOfImage: String
            listOfAllImages = ArrayList()

            while (cursor.moveToNext()) {
                val galleryModel: GalleryModel = GalleryModel()
                absolutePathOfImage = cursor.getString(column_index_data)
                val ab = absolutePathOfImage.split("/")
                val length = absolutePathOfImage.split("/").size
                galleryModel.image = absolutePathOfImage
                galleryModel.imageName = ab[length - 1]
                listOfAllImages.add(galleryModel)

            }
            Log.d("dcdvhdbhv",listOfAllImages.size.toString() + "  ,  "+listOfAllImages.get(1).image)
            phoneGalleryAdapter = PhoneGalleryAdapter(requireActivity(), this as PhoneGalleryFragment, listOfAllImages)
            recyclerView.adapter = phoneGalleryAdapter
        }
        catch (e:Exception)
        {}

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PhoneGalleryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
    override fun showGalleryNextButton(gallerySelectedContentSize: Int, galleryImage: String) {
     if(gallerySelectedContentSize>0)
         textView_roboto_boldNext.setVisibility(View.VISIBLE)
    else
     {
         textView_roboto_boldNext.setVisibility(View.GONE)
     }
      this.galleryImageNew=galleryImage
    }
}