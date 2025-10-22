package com.sap.codelab.view.create

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.sap.codelab.DI
import com.sap.codelab.location.LocationProvider
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.IGeofenceRepository
import com.sap.codelab.repository.IMemoRepository
import com.sap.codelab.repository.Repository
import com.sap.codelab.utils.coroutines.ScopeProvider
import com.sap.codelab.utils.extensions.empty
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for matching CreateMemo view. Handles user interactions.
 */
internal class CreateMemoViewModel(
    //TODO: Inject
    private val repository: IMemoRepository = DI.memoRepository,
    private val geofenceRepository: IGeofenceRepository = DI.geofenceRepository,
    private val locationProvider: LocationProvider = DI.locationProvider,
) : ViewModel() {


    private var memo = Memo(0, String.empty(), String.empty(), 0, 0.0, 0.0, false)

    val memos: StateFlow<List<Memo>> = repository.getOpen()
        .map { list -> list.filter { !it.isDone && it.reminderLatitude != 0.0 && it.reminderLongitude != 0.0 } } //TODO no location check
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    val myLocation: StateFlow<Location?> =
        flow { emit(locationProvider.getLastKnownLocation(false)) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)


    /**
     * Saves the memo in it's current state.
     */
    fun saveMemo() {
        ScopeProvider.application.launch {
            val id = repository.saveMemo(memo)
            geofenceRepository.addGeofence(memo.copy(id = id))
        }
    }

    /**
     * Call this method to update the memo. This is usually needed when the user changed his input.
     */
    fun updateMemo(title: String, description: String, latLng: LatLng) {
        memo = Memo(
            title = title,
            description = description,
            id = 0,
            reminderDate = 0,
            reminderLatitude = latLng.latitude,
            reminderLongitude = latLng.longitude,
            isDone = false
        )
    }

    /**
     * @return true if the title and content are not blank; false otherwise.
     */
    fun isMemoValid(): Boolean = memo.title.isNotBlank() && memo.description.isNotBlank()

    /**
     * @return true if the memo text is blank, false otherwise.
     */
    fun hasTextError() = memo.description.isBlank()

    /**
     * @return true if the memo title is blank, false otherwise.
     */
    fun hasTitleError() = memo.title.isBlank()
}