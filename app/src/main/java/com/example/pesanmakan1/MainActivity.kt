package com.example.pesanmakan1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pesanmakan1.adapter.BeverageAdapater
import com.example.pesanmakan1.eventbus.UpdateCartEvent
import com.example.pesanmakan1.listener.BeverageLoadListener
import com.example.pesanmakan1.listener.CartLoadListener
import com.example.pesanmakan1.model.BeverageModel
import com.example.pesanmakan1.model.CartModel
import com.example.pesanmakan1.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nex3z.notificationbadge.NotificationBadge
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MainActivity : AppCompatActivity(), BeverageLoadListener, CartLoadListener{

    lateinit var badge : NotificationBadge
    lateinit var cartLoadListener : CartLoadListener
    lateinit var bevRecyclerView: RecyclerView
    lateinit var mainLayout : View
    lateinit var beverageLoadListener : BeverageLoadListener
    lateinit var btnCart : View
    lateinit var btnProf : View

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
     fun onUpdateCartEvent(event:UpdateCartEvent)
    {
        counCartFromFirebase()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        badge = findViewById(R.id.badge)
        mainLayout = findViewById(R.id.mainLayout)
        bevRecyclerView = findViewById(R.id.recycler_beverage)

        btnCart = findViewById(R.id.btnCart)
        btnProf = findViewById(R.id.btnProfile)



        init()
        loadBeverageFromFirebase()
        counCartFromFirebase()

    }

    private fun counCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key =cartSnapshot.key
                        cartModels.add(cartModel)

                    }
                    cartLoadListener.onLoadCartSucces(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onLoadCartFailed(error.message)
                }

            })
    }

    private fun loadBeverageFromFirebase() {
        val beverageModels : MutableList<BeverageModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Drink")
            .addListenerForSingleValueEvent(object : ValueEventListener  {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists())
                    {
                        for (beverageSnapshot in snapshot.children)
                        {
                            val beverageModel = beverageSnapshot.getValue(BeverageModel::class.java)
                            beverageModel!!.key = beverageSnapshot.key
                            beverageModels.add(beverageModel)
                        }
                        beverageLoadListener.onBeverageLoadSuccess(beverageModels)
                    }
                    else
                        beverageLoadListener.onBeverageLoadFailed("Beverage items not exists")
                }

                override fun onCancelled(error: DatabaseError) {
                    beverageLoadListener.onBeverageLoadFailed(error.message)
                }

            })

    }

    private fun init(){
        beverageLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        bevRecyclerView.layoutManager = gridLayoutManager
        bevRecyclerView.addItemDecoration(SpaceItemDecoration())


        btnCart.setOnClickListener{startActivity(Intent(this,CartActivity::class.java))}
        btnProf.setOnClickListener{startActivity(Intent(this,UserActivity::class.java))}


    }



    override fun onBeverageLoadSuccess(beverageModelList: List<BeverageModel>?) {
        val adapter = BeverageAdapater(this,beverageModelList!!,cartLoadListener)
        bevRecyclerView.adapter = adapter
    }

    override fun onBeverageLoadFailed(message: String?) {

        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()

    }

    override fun onLoadCartSucces(cartModelList: List<CartModel>) {
        var cartSum = 0
        for (cartModel in cartModelList!!) cartSum += cartModel!!.quantity
        badge!!.setNumber(cartSum)
    }

    override fun onLoadCartFailed(message: String?) {

        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }



}