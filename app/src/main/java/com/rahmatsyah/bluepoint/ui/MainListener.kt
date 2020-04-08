package com.rahmatsyah.bluepoint.ui

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.rahmatsyah.bluepoint.model.BluePoint

interface MainListener{
    interface View{
        fun showLocation(latLng: LatLng = LatLng(-6.2088,106.8456))
        fun initRandomPoint(bluePoints:ArrayList<BluePoint>)
    }
    interface Presenter{
        fun requestLocation(context:Context)
        fun requestRandomBluePoint(latLng: LatLng)
    }
}
