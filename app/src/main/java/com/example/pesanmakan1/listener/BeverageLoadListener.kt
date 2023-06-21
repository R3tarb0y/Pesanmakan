package com.example.pesanmakan1.listener

import com.example.pesanmakan1.model.BeverageModel

interface BeverageLoadListener {
    fun onBeverageLoadSuccess (beverageModelList:List<BeverageModel>?)
    fun onBeverageLoadFailed (message:String?)
}