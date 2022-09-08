package com.bignerdranch.android.navigationcomponenttabs.screens.main.tabs.settings

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.bignerdranch.android.navigationcomponenttabs.R
import com.bignerdranch.android.navigationcomponenttabs.model.StorageException
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.BoxesRepository
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.Box
import com.bignerdranch.android.navigationcomponenttabs.model.boxes.entities.BoxAndSettings
import com.bignerdranch.android.navigationcomponenttabs.utils.MutableLiveEvent
import com.bignerdranch.android.navigationcomponenttabs.utils.publishEvent
import com.bignerdranch.android.navigationcomponenttabs.utils.share


class SettingsViewModel(
    private val boxesRepository: BoxesRepository
) : ViewModel(), SettingsAdapter.Listener {

    private val _boxSettings = MutableLiveData<List<BoxAndSettings>>()
    val boxSettings = _boxSettings.share()

    private val _showErrorMessageEvent = MutableLiveEvent<Int>()
    val showErrorMessageEvent =_showErrorMessageEvent.share()

    init {
        viewModelScope.launch {
            boxesRepository.getBoxesAndSettings().collect {
                _boxSettings.value = it
            }
        }
    }

    override fun enableBox(box: Box) {
        viewModelScope.launch {
            try {
                boxesRepository.activateBox(box)
            } catch (e: StorageException) {
                showStorageErrorMessage()
            }
        }
    }

    override fun disableBox(box: Box) {
        viewModelScope.launch {
            try {
                boxesRepository.deactivateBox(box)
            } catch (e: StorageException) {
                showStorageErrorMessage()
            }
        }
    }

    private fun showStorageErrorMessage() {
        _showErrorMessageEvent.publishEvent(R.string.storage_error)
    }
}