package com.example.onlineacadezykotlin

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager


class CustomLayoutmanager(context: Context?) : LinearLayoutManager(context)
{
    var isScrollEnabled:Boolean =true


    fun setScrollingEnableds(flag :Boolean)
    {
        isScrollEnabled=flag
    }

    override fun canScrollVertically(): Boolean {
        return  isScrollEnabled && super.canScrollVertically()
    }
}

