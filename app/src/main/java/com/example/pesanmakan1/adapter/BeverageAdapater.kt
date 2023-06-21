package com.example.pesanmakan1.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pesanmakan1.R
import com.example.pesanmakan1.eventbus.UpdateCartEvent
import com.example.pesanmakan1.listener.CartLoadListener
import com.example.pesanmakan1.listener.RecylcerClickListener
import com.example.pesanmakan1.model.BeverageModel
import com.example.pesanmakan1.model.CartModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import java.lang.StringBuilder

class BeverageAdapater (
    private val context: Context,
    private val list: List<BeverageModel>,
    private val cartListener : CartLoadListener
): RecyclerView.Adapter<BeverageAdapater.BeverageViewHolder>(){


        class BeverageViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView),
            View.OnClickListener {
            var imageView:ImageView?=null
            var txtName: TextView?=null
            var txtPrice:TextView?=null

            private var clickListener :RecylcerClickListener? =null

            fun setClickListener(clickListener: RecylcerClickListener)
            {
                this.clickListener = clickListener
            }

            init {
                imageView = itemView.findViewById(R.id.imageView) as ImageView
                txtName = itemView.findViewById(R.id.txtName) as TextView
                txtPrice = itemView.findViewById(R.id.txtPrice) as TextView


                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View?) {
                clickListener!!.onItemClickListener(v,adapterPosition)
            }


        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BeverageViewHolder {
            return BeverageViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.beverage_item,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: BeverageViewHolder, position: Int) {
            Glide.with(context)
                .load(list[position].image)
                .into(holder.imageView!!)
            holder.txtName!!.text = StringBuilder().append(list[position].name)
            holder.txtPrice!!.text = StringBuilder("Rp ").append(list[position].price)

            holder.setClickListener(object :RecylcerClickListener{
                override fun onItemClickListener(view: View?, position: Int) {
                    addToCart(list[position])
                }

            })
    }

    private fun addToCart(beverageModel: BeverageModel) {
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")

        userCart.child(beverageModel.key!!)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.exists())
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData : MutableMap <String,Any> = HashMap ()

                        cartModel!!.quantity +1
                        updateData["quantity"] = cartModel!!.quantity +1
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()


                        userCart.child(beverageModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed( "Succes add to cart")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)

                            }
                    }
                    else
                    {
                        val cartModel = CartModel()
                        cartModel.key = beverageModel.key
                        cartModel.name = beverageModel.name
                        cartModel.image = beverageModel.image
                        cartModel.price = beverageModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = beverageModel.price!!.toFloat()

                        userCart.child(beverageModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onLoadCartFailed( "Succes add to cart")
                            }
                            .addOnFailureListener{e-> cartListener.onLoadCartFailed(e.message)

                            }

                    }
                }


                override fun onCancelled(error: DatabaseError) {
                    cartListener.onLoadCartFailed(error.message)
                }

            })
    }


}