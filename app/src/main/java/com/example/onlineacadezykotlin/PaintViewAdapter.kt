package com.example.onlineacadezykotlin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.onlineacadezykotlin.PaintViewAdapter.*

class PaintViewAdapter(private val context:Context,val whiteBoardFragment: WhiteBoardFragment) : RecyclerView.Adapter<MyHandler>() {
  lateinit var abc:String
    var communicateWithActivityInterface:CommunicateWithActivityInterface=whiteBoardFragment

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHandler {
        val view :View=LayoutInflater.from(context).inflate(R.layout.paint_view_adapter,parent,false)
        val myHandler:MyHandler= MyHandler(view);
        return myHandler;
    }

    override fun getItemCount(): Int {
        return 1
    }

    override fun onBindViewHolder(holder: MyHandler, position: Int) {
        communicateWithActivityInterface.CommunicateWithActivity(holder.paintView)
    }

     class MyHandler(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var paintView:PaintView=itemView.findViewById(R.id.paintView);
    }

    interface CommunicateWithActivityInterface
    {
       fun  CommunicateWithActivity(paintView: PaintView)
    }
}