package com.rahmatsyah.bluepoint.ui

import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.rahmatsyah.bluepoint.R
import com.rahmatsyah.bluepoint.model.BluePoint
import kotlinx.android.synthetic.main.view_blue_point.view.*
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class MainAdapter(private val context:Context,private val latLng: LatLng, private val radius:Double) : RecyclerView.Adapter<MainAdapter.ViewHolder>() {

    private val bluePoints = arrayListOf<BluePoint>()

    fun addBluePoint(bluePoint: BluePoint){
        bluePoints.add(0,bluePoint)
        notifyItemInserted(0)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        ViewHolder(LayoutInflater.from(context).inflate(R.layout.view_blue_point,parent,false),latLng,radius)

    override fun getItemCount(): Int = bluePoints.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(bluePoints[position])
    }

    inner class ViewHolder(itemView:View,private val latLng: LatLng,private val radius: Double):RecyclerView.ViewHolder(itemView) {
        fun bind(bluePoint: BluePoint){
            itemView.rbInRadius.isChecked = distance(LatLng(bluePoint.latitude,bluePoint.longitude),latLng) <= radius
            itemView.tvBluePointName.text = bluePoint.name
            itemView.tvBluePointLatitude.text = bluePoint.latitude.toString()
            itemView.tvBluePointLongitude.text = bluePoint.longitude.toString()
        }
        private fun distance(latLng1: LatLng,latLng2: LatLng):Double{
            val loc1 = Location("")
            loc1.latitude = latLng1.latitude
            loc1.longitude = latLng1.longitude
            val loc2 = Location("")
            loc2.latitude = latLng2.latitude
            loc2.longitude = latLng2.longitude
            return loc1.distanceTo(loc2).toDouble()
        }
    }
}