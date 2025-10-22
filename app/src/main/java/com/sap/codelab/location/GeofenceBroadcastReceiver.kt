package com.sap.codelab.location


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sap.codelab.DI
import com.sap.codelab.notification.NotificationHelper
import com.sap.codelab.repository.IMemoRepository
import com.sap.codelab.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class GeofenceBroadcastReceiver : BroadcastReceiver() {

    private val repository : IMemoRepository = DI.memoRepository
    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent) ?: return

        if (geofencingEvent.hasError()) {
            return
        }

        if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            val requestIdString = geofencingEvent.triggeringGeofences?.firstOrNull()?.requestId ?: return
            val requestIdLong = requestIdString.toLongOrNull() ?: return

            receiverScope.launch {
                try {
                    val memo = repository.getMemoById(requestIdLong)
                    NotificationHelper.showMemoNotification(context, memo)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}