package com.sap.codelab.repository

import com.sap.codelab.model.Memo

internal interface IGeofenceRepository {
    suspend fun addGeofence(memo: Memo)
    suspend fun removeGeofence(id: Long)
}