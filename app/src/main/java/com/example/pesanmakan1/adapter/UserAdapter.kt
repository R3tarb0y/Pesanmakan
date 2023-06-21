package com.example.pesanmakan1.adapter

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pesanmakan1.R
import com.example.pesanmakan1.eventbus.UpdateCartEvent
import com.example.pesanmakan1.model.UserModel
import com.google.firebase.database.FirebaseDatabase
import org.greenrobot.eventbus.EventBus


class UserAdapter (private val usList: ArrayList<UserModel>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(itemView, mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentEmp = usList[position]
        holder.tvUsName.text = currentEmp.usName
        holder.tvUsMeja.text = currentEmp.usMeja


    }

    override fun getItemCount(): Int {
        return usList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvUsName : TextView = itemView.findViewById(R.id.tvUsName)
        val tvUsMeja : TextView = itemView.findViewById(R.id.tvUsMeja)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }

    }

}