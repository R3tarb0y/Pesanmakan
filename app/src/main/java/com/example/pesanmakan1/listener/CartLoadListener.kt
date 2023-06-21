package com.example.pesanmakan1.listener

import com.example.pesanmakan1.model.CartModel

interface CartLoadListener {
    fun onLoadCartSucces(cartModelList : List<CartModel>)
    fun onLoadCartFailed(message : String?)
}