package com.noemi.movieinspector.screens.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor() : ViewModel() {

    private var _networkState = MutableStateFlow(false)
    val networkState = _networkState.asStateFlow()

    fun onNetworkStateChanged(isActive: Boolean) {
        viewModelScope.launch {
            _networkState.emit(isActive)
        }
    }
}