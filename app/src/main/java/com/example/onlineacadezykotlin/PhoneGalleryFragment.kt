package com.example.onlineacadezykotlin

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onlineacadezykotlin.databinding.FragmentPhoneGalleryBinding
import java.lang.Exception

class PhoneGalleryFragment : Fragment(), PhoneGalleryAdapter.ShowGalleryNextButtonInterface {
    // TODO: Rename and change types of parameters
    private var selected_folder: Int? = null
    lateinit var phoneGalleryAdapter: PhoneGalleryAdapter;
    lateinit var listOfAllImages: ArrayList<GalleryModel>
    lateinit var listOfRelatedFolderImages: ArrayList<GalleryModel>
    lateinit var galleryImageNewList: List<String>
    lateinit var navController: NavController
    private var fragmentPhoneGalleryBinding:FragmentPhoneGalleryBinding?=null
    private val binding get() = fragmentPhoneGalleryBinding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            selected_folder = it.getInt("selected_folder")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        fragmentPhoneGalleryBinding= FragmentPhoneGalleryBinding.inflate(inflater,container,false)
        val view =binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.GONE
        binding.nextText.setEnabled(true)
        navController = Navigation.findNavController(view)
        binding.recyclerView.setHasFixedSize(true)
        val gridLayoutManager = GridLayoutManager(requireActivity(), 3);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL); // set Horizontal Orientation
        binding.recyclerView.setLayoutManager(gridLayoutManager)
        getGalleryData()

        binding.nextText.setOnClickListener {
            navController.previousBackStackEntry?.savedStateHandle?.set("key", "selected")
            StaticClass.selectedListOfImages.addAll(galleryImageNewList)
            binding.progressbarNew.visibility=View.VISIBLE
            binding.nextText.setTextColor(ContextCompat.getColor(requireActivity(), R.color.colorPrimary))
            navController.navigateUp()
        }
    }

    fun getGalleryData() {
        try {
            val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projection =
                arrayOf(MediaStore.MediaColumns.DATA, MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val cursor: Cursor? = requireActivity().getContentResolver()
                .query(uri, projection, null, null, MediaStore.Images.Media.DATE_TAKEN + " DESC")
            val column_index_data: Int =
                cursor!!.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
            var absolutePathOfImage: String
            listOfAllImages = ArrayList()
            listOfRelatedFolderImages= ArrayList()

            while (cursor.moveToNext()) {
                val galleryModel: GalleryModel = GalleryModel()
                absolutePathOfImage = cursor.getString(column_index_data)
                val ab = absolutePathOfImage.split("/")
                val length = absolutePathOfImage.split("/").size
                galleryModel.image = absolutePathOfImage
                galleryModel.imageName = ab[length - 1]
                listOfAllImages.add(galleryModel)

            }
            when (selected_folder) {
                1 -> {
                    for (i in 0 .. (listOfAllImages.size/3)-1)
                    {
                    listOfRelatedFolderImages.add(listOfAllImages.get(i))
                    }
                }
                2 -> {
                    for (i in (listOfAllImages.size/3) .. (2*listOfAllImages.size/3)-1)
                    {
                        listOfRelatedFolderImages.add(listOfAllImages.get(i))
                    }
                }
                else -> { // Note the block
                    for (i in (2*listOfAllImages.size/3) .. listOfAllImages.size-1)
                    {
                        listOfRelatedFolderImages.add(listOfAllImages.get(i))
                    }
                }
            }

            phoneGalleryAdapter = PhoneGalleryAdapter(
                requireActivity(),
                this as PhoneGalleryFragment,
                listOfRelatedFolderImages
            )
            binding.recyclerView.adapter = phoneGalleryAdapter
        } catch (e: Exception) {
        }
    }

    override fun showGalleryNextButton(gallerySelectedContentSize: Int, galleryImageList: List<String>) {
        if (gallerySelectedContentSize > 0)
            binding.nextText.visibility = View.VISIBLE
        else {
            binding.nextText.visibility = View.GONE
        }
        this.galleryImageNewList = galleryImageList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragmentPhoneGalleryBinding=null
    }
}