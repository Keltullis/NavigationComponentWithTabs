package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.utils.MutableLiveEvent
import com.bignerdranch.android.navigationcomponenttabs.utils.publishEvent
import com.bignerdranch.android.navigationcomponenttabs.utils.share

class BoxViewModel(
    private val boxId: Long,
    private val boxesRepository: BoxesRepository
) : ViewModel() {

    private val _shouldExitEvent = MutableLiveEvent<Boolean>()
    val shouldExitEvent = _shouldExitEvent.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings(onlyActive = true)
                .map { boxes -> boxes.firstOrNull { it.box.id == boxId } }
                .collect { currentBox ->
                    _shouldExitEvent.publishEvent(currentBox == null)
                }
        }
    }
}