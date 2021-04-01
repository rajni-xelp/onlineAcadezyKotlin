package com.example.onlineacadezykotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ZoomImageListFragment : Fragment() , AdapterZoomimageList.SendPositionInterface {
    lateinit var imageList :List<String>
    lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageList = it.getStringArrayList("image_list")!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_zoom_image_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController= Navigation.findNavController(view)
       val rv_zoomlist=view.findViewById<RecyclerView>(R.id.rv_zoomlist)
        val gridLayoutManager = GridLayoutManager(requireActivity(), 2)
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL) // set Horizontal Orientation
        val adapterZoomimageList=AdapterZoomimageList(requireActivity(),imageList,this@ZoomImageListFragment)
        rv_zoomlist.setLayoutManager(gridLayoutManager)
        rv_zoomlist.adapter=adapterZoomimageList
        observeResult()

    }

    override fun sendPosition(position: Int) {
        val bundle= Bundle()
        bundle.putString("imageUri",imageList.get(position))
        StaticClass.zoomed_position=position
        navController.navigate(R.id.action_zoomImageListFragment_to_chooseImageToZoom,bundle)
    }

    fun observeResult()
    {
        val liveData: MutableLiveData<String> =
            navController.currentBackStackEntry?.getSavedStateHandle()
                ?.getLiveData<String>("key") as MutableLiveData<String>
        liveData.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String?) {
                // Do something with the result.
                if (s != null) {
                    navController.previousBackStackEntry?.savedStateHandle?.set("key", "zoomed_image")
                    navController.navigateUp()
                }
            }
        })
    }
}