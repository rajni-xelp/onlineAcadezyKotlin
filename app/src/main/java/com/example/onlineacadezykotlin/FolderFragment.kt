package com.example.onlineacadezykotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation

class FolderFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    lateinit var navController: NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_folder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rl_recent=view.findViewById<RelativeLayout>(R.id.rl_recent)
        val rl_camera=view.findViewById<RelativeLayout>(R.id.rl_camera)
        val rl_screenshot=view.findViewById<RelativeLayout>(R.id.rl_screenshot)
        navController = Navigation.findNavController(view)
        rl_recent.setOnClickListener {
            val bundle= Bundle()
            bundle.putInt("selected_folder",1)
            navController.navigate(R.id.action_folderFragment_to_phoneGalleryFragment,bundle)
        }

        rl_camera.setOnClickListener {
            val bundle= Bundle()
            bundle.putInt("selected_folder",2)
            navController.navigate(R.id.action_folderFragment_to_phoneGalleryFragment,bundle)
        }

        rl_screenshot.setOnClickListener {
            val bundle= Bundle()
            bundle.putInt("selected_folder",3)
            navController.navigate(R.id.action_folderFragment_to_phoneGalleryFragment,bundle)
        }

        val liveData: MutableLiveData<String> =
            navController.currentBackStackEntry?.getSavedStateHandle()
                ?.getLiveData<String>("key") as MutableLiveData<String>
        liveData.observe(viewLifecycleOwner, object : Observer<String?> {
            override fun onChanged(s: String?) {
                // Do something with the result.
                if (s != null) {
                    Log.d("scjsdfsf", StaticClass.selectedListOfImages.size.toString())
                    navController.previousBackStackEntry?.savedStateHandle?.set("key", "selected")
                    navController.navigateUp()
                }
            }
        })
    }

}