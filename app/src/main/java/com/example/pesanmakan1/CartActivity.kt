package com.example.pesanmakan1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pesanmakan1.adapter.CartAdapter
import com.example.pesanmakan1.eventbus.UpdateCartEvent
import com.example.pesanmakan1.listener.CartLoadListener
import com.example.pesanmakan1.model.CartModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.lang.StringBuilder

class CartActivity : AppCompatActivity(), CartLoadListener {

    lateinit var totalTxt : TextView
    var cartLoadListener:CartLoadListener?=null
    private lateinit var recycler_cart : RecyclerView
    lateinit var btnback : ImageView
    lateinit var mainLayout : View
    lateinit var btnOrder : Button


    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        if(EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
     fun onUpdateCartEvent(event: UpdateCartEvent)
    {
        loadCartFromFirebase()
    }





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        mainLayout = findViewById(R.id.mainLayout)
        totalTxt = findViewById(R.id.totalTxt)
        btnback = findViewById(R.id.btnBack)
        recycler_cart = findViewById(R.id.recycler_cart)
        btnOrder = findViewById(R.id.buttonOrder)
        init()
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key =cartSnapshot.key
                        cartModels.add(cartModel)

                    }
                    cartLoadListener!!.onLoadCartSucces(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onLoadCartFailed(error.message)
                }

            })
    }

    private fun init() {
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnback!!.setOnClickListener{finish()}

        btnOrder.setOnClickListener{
            val intent = Intent(this, CompletActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onLoadCartSucces(cartModelList: List<CartModel>) {
        var sum = 0.0
        for (cartModel in cartModelList!!){
            sum+= cartModel!!.totalPrice
        }
        totalTxt.text = StringBuilder("Rp ").append(sum)
        val adapter = CartAdapter(this,cartModelList)
        recycler_cart!!.adapter = adapter
    }

    override fun onLoadCartFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }
}