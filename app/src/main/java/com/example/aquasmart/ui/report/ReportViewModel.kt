package com.example.aquasmart.ui.report

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ReportViewModel : ViewModel() {
    private val _text = MutableLiveData<String>().apply {
        value = "This is Fragment report"
    }
    val text: LiveData<String> = _text
}