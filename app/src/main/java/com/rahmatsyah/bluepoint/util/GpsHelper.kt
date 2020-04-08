package com.rahmatsyah.bluepoint.util

import android.content.Context
import android.location.LocationManager

object GpsHelper {
    fun isGpsEnable(context: Context):Boolean{
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}