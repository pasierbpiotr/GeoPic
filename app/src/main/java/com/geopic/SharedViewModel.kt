package com.geopic

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val image: MutableLiveData<Bitmap> = MutableLiveData()
}