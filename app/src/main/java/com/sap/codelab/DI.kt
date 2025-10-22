package com.sap.codelab

import android.annotation.SuppressLint
import android.content.Context
import com.sap.codelab.location.GeofenceRepository
import com.sap.codelab.location.LocationProvider
import com.sap.codelab.repository.IGeofenceRepository
import com.sap.codelab.repository.IMemoRepository
import com.sap.codelab.repository.Repository


@SuppressLint("StaticFieldLeak")
internal object DI {

    lateinit var context: Context
    fun initialize(appContext: Context) {
        context = appContext
    }

    val memoRepository: IMemoRepository by lazy { Repository(context) }

    val geofenceRepository: IGeofenceRepository by lazy { GeofenceRepository(context) }

    val locationProvider: LocationProvider by lazy { LocationProvider(context) }

}