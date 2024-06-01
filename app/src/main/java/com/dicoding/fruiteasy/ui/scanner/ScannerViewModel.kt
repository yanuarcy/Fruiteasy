package com.dicoding.fruiteasy.ui.scanner

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is scanner Fragment"
    }
    val text: LiveData<String> = _text

    private val _isBottomNavVisible = MutableLiveData(true)
    val isBottomNavVisible: LiveData<Boolean> get() = _isBottomNavVisible

    fun showBottomNav() {
        _isBottomNavVisible.value = true
    }

    fun hideBottomNav() {
        _isBottomNavVisible.value = false
    }
}