package com.mobicomp.labs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver {

    override fun onReceive(context: Context?, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        val geofencingTransition = geofencingEvent.geofenceTransition

        if(geofencingTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofencingTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            var uId = intent!!.getIntExtra("uId", 0)
            var text = intent.getStringExtra("message")

            MainActivity.showNotification(context!!, text)
        }
    }
}