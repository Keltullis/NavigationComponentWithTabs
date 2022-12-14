package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs.dashboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.Box
import com.bignerdranch.android.navigationcomponenttabs.utils.share

class DashboardViewModel(
    private val boxesRepository: BoxesRepository
) : ViewModel() {

    private val _boxes = MutableLiveData<List<Box>>()
    val boxes = _boxes.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxes(onlyActive = true).collect {
                _boxes.value = it
            }
        }
    }

}