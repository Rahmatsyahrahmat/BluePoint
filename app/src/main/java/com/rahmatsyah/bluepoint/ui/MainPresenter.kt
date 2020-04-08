package com.rahmatsyah.bluepoint.ui

import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.rahmatsyah.bluepoint.model.BluePoint
import com.rahmatsyah.bluepoint.ui.MainListener
import kotlin.math.*
import kotlin.random.Random

class MainPresenter(private val view: MainListener.View):
    MainListener.Presenter{

    override fun requestLocation(context: Context) {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if(location!=null)
                view.showLocation(LatLng(location.latitude,location.longitude))
            else
                view.showLocation()
        }.addOnFailureListener {
            view.showLocation()
        }
    }

    override fun requestRandomBluePoint(latLng: LatLng) {
        val randomBluePoints = arrayListOf<BluePoint>()

        for (i in 0..7){
            val x0 = latLng.latitude
            val y0 = latLng.longitude

            val radiusInMeters = 300.0*(i+1)
            val radiusInDegrees = radiusInMeters/111000f

            val u = Random.nextDouble()
            val v = Random.nextDouble()
            val w = radiusInDegrees * sqrt(u)
            val t = 2 * PI * v
            var x = w * cos(t)
            val y = w * sin(t)

            x /= cos(y0)

            val lat = x + x0
            val long = y + y0

            randomBluePoints.add(BluePoint("Blue Point ${i+1}",lat,long))

        }
        view.initRandomPoint(randomBluePoints)
    }

}