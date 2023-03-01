package com.example.composition_v2.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.domain.entity.Level

class GameFragmentViewModelFactory(
    private val application: Application,
    private val level: Level
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameFragmentViewModel::class.java))
            return GameFragmentViewModel(application, level) as T
        else throw RuntimeException("Unknown view model class $modelClass")
    }
}