package com.example.onlineacadezykotlin

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation

class MainFragment : Fragment() ,View.OnClickListener{
   lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        StaticClass.draw=false
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController=Navigation.findNavController(view)
        view.findViewById<LinearLayoutCompat>(R.id.bt_createWhiteBoard).setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when(v!!.id)
        {
            R.id.bt_createWhiteBoard->navController.navigate(R.id.action_mainFragment_to_whiteBoardFragment)
        }
    }
}