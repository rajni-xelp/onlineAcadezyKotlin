package com.example.onlineacadezykotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class WhiteBoardViewModel(state:SavedStateHandle) : ViewModel() {
  //  lateinit var
  companion object
  {
   private val student_id_key="student_id_key"
  }

   private val savedStateHandle=state
   val student_id_livedat:MutableLiveData<String> = savedStateHandle.getLiveData("student_id_key")

    fun setCurrentUser()
    {
        student_id_livedat.value="3"
    }

}