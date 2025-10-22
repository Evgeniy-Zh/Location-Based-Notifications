package com.sap.codelab.location

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.sap.codelab.model.Memo
import com.sap.codelab.repository.IGeofenceRepository
import kotlinx.coroutines.tasks.await

internal class GeofenceRepository(private val context: Context): IGeofenceRepository {

    private val geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)

    private val geofencePendingIntent: PendingIntent by lazy {

        val intent = Intent(context.applicationContext, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    }

    override suspend fun addGeofence(
        memo: Memo
    ) {
        val fineLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val backgroundLocationGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!fineLocationGranted || !backgroundLocationGranted) {
            return
        }

        val geofence = Geofence.Builder()
            .setRequestId(memo.id.toString())
            .setCircularRegion(memo.reminderLatitude, memo.reminderLongitude, 200f)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .build()

        val request = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(request, geofencePendingIntent).addOnSuccessListener {
            Log.d("GEOFENCE", "ADDED")
        }.addOnFailureListener {
            Log.d("GEOFENCE", "ADD FAILED")
        }.await()

    }

    override suspend fun removeGeofence(id: Long) {
        geofencingClient.removeGeofences(listOf(id.toString())).addOnSuccessListener {
            Log.d("GEOFENCE", "REMOVED")
        }.addOnFailureListener {
            Log.d("GEOFENCE", "REMOVE FAILED")
        }.await()
    }
}