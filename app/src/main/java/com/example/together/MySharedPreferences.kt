package com.example.together

import android.content.Context

class MySharedPreferences {
    val MY_SHARED_PREFERENCES = "MY_SHARED_PREFERENCES"
    lateinit var mContext : Context
    constructor(mContext : Context){
        this.mContext = mContext
    }


}