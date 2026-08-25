package com.symbolsense.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.symbolsense.data.local.SymbolSenseDatabase
import com.symbolsense.data.model.ScanResult
import com.symbolsense.data.repository.ScanRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ScanHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScanRepository(
        SymbolSenseDatabase.getInstance(application)
    )

    val history: StateFlow<List<ScanResult>> = repository.observeHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun saveScan(result: ScanResult, onSaved: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            val id = repository.saveScan(result)
            onSaved?.invoke(id)
        }
    }

    fun deleteScan(scanId: String, onDeleted: (() -> Unit)? = null) {
        viewModelScope.launch {
            repository.deleteScan(scanId)
            onDeleted?.invoke()
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
