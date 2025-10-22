package com.sap.codelab.view.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sap.codelab.DI
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.IGeofenceRepository
import com.sap.codelab.repository.IMemoRepository
import com.sap.codelab.repository.Repository
import com.sap.codelab.utils.coroutines.ScopeProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home Activity.
 */
internal class HomeViewModel(
    private val repository : IMemoRepository = DI.memoRepository,
    private val geofenceRepository: IGeofenceRepository = DI.geofenceRepository
) : ViewModel() {

    private val isShowAll = MutableStateFlow(false)

    val memos: StateFlow<List<Memo>> = isShowAll.flatMapLatest { value->
        if(value) repository.getAll() else repository.getOpen()
    }.stateIn(viewModelScope, started = SharingStarted.WhileSubscribed(), emptyList())

    /**
     * Loads all memos.
     */
    fun loadAllMemos() {
        isShowAll.value = true
    }

    /**
     * Loads all open (not done) memos.
     */
    fun loadOpenMemos() {
        isShowAll.value = false
    }

    /**
     * Updates the given memo, marking it as done if isChecked is true.
     *
     * @param memo      - the memo to update.
     * @param isChecked - whether the memo has been checked (marked as done).
     */
    fun updateMemo(memo: Memo, isChecked: Boolean) {
        ScopeProvider.application.launch(Dispatchers.Default) {
            // We'll only forward the update if the memo has been checked, since we don't offer to uncheck memos right now
            if (isChecked) {
                repository.saveMemo(memo.copy(isDone = true))
                geofenceRepository.removeGeofence(memo.id)
            }
        }
    }
}