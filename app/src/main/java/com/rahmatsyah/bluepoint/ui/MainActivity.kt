package com.rahmatsyah.bluepoint.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.rahmatsyah.bluepoint.R
import com.rahmatsyah.bluepoint.model.BluePoint
import com.rahmatsyah.bluepoint.util.GpsHelper
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),OnMapReadyCallback,
    MainListener.View {

    private val mapView by lazy {
        supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
    }

    private var map:GoogleMap? = null
    private var  radius:Double = 1000.0

    private lateinit var latLng: LatLng
    private lateinit var circleRadius:Circle

    private val REQUEST_LOCATION_PERMISSION = 1
    private val reqGPS = 9004

    private lateinit var presenter: MainPresenter

    private lateinit var adapter: MainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setDisplayShowCustomEnabled(true)
        supportActionBar?.setCustomView(R.layout.app_bar_main)

        presenter = MainPresenter(this)
        rvBluePoint.layoutManager = LinearLayoutManager(this)

        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        this.map = map
        if (GpsHelper.isGpsEnable(this)){
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
            else
                presenter.requestLocation(this)
        }else{
            showRequestGpsAlert()
        }
        map?.setOnMapLongClickListener {
            addBluePoint(BluePoint("Blue Point ${adapter.itemCount+1}",it.latitude,it.longitude))
        }

    }

    override fun showLocation(latLng: LatLng) {
        this.latLng = latLng
        map?.let {map->
            val cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng,14f)
            map.moveCamera(cameraUpdate)

            circleRadius = map.addCircle(CircleOptions().center(latLng).radius(radius).fillColor(0x110000FF).strokeColor(0xFF0000FF.toInt()).strokeWidth(1f))
        }
        adapter = MainAdapter(this,latLng,radius)
        rvBluePoint.adapter = adapter
        presenter.requestRandomBluePoint(latLng)
    }

    override fun initRandomPoint(bluePoints: ArrayList<BluePoint>) {
        bluePoints.forEach {
            addBluePoint(it)
        }
    }

    private fun addBluePoint(bluePoint: BluePoint){
        map?.addMarker(MarkerOptions().icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_point)).position(LatLng(bluePoint.latitude,bluePoint.longitude)).title(bluePoint.name))
        adapter.addBluePoint(bluePoint)
        rvBluePoint.smoothScrollToPosition(0)
    }

    private fun showRequestGpsAlert(){
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setMessage("Nyalakan GPS?")
        alertDialog.setPositiveButton("Iya"
        ) { dialog, _ ->
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, reqGPS)
            dialog.dismiss()
        }
        alertDialog.setNegativeButton("Tidak"
        ) { dialog, _ ->
            showLocation()
            dialog.dismiss()
        }
        alertDialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode==reqGPS){
            if (GpsHelper.isGpsEnable(this)) {
                if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
                else {
                    Thread.sleep(1000)
                    presenter.requestLocation(this)
                }
            }
            else
                showLocation()
        }
    }


//    override fun onMapReady(googleMap: GoogleMap?) {
//        map = googleMap
//        map?.setMaxZoomPreference(17f)
//        setLocation(LatLng(-6.2088,106.8456))
////        map?.addCircle(CircleOptions().center(LatLng(-6.2088,106.8456)).radius(200.0).strokeWidth(1f).fillColor(0x66aaaFFF))
////        drawRadius(-6.2088,106.8456,200.0)
//
//
//        val topLeft = map?.projection?.visibleRegion?.farLeft
//        val topRight = map?.projection?.visibleRegion?.farRight
//
//
//
//        var distance = FloatArray(1)
//        Location.distanceBetween(topLeft!!.latitude,topLeft!!.longitude,topRight!!.latitude,topRight!!.longitude,distance)
//        radius = (distance[0].toDouble()/4).toDouble()
//
//        Log.i("oioioioi",radius.toString())
//
////        map?.addCircle(CircleOptions().center(map?.cameraPosition?.target).radius(1000.0).fillColor(0x110000FF).strokeColor(0xFF0000FF.toInt()).strokeWidth(1f))
//
//        map?.setOnMapLongClickListener {
//            map?.addMarker(MarkerOptions().position(it).icon(BitmapDescriptorFactory.fromResource(R.drawable.blue_point)))
//        }
//
//        map?.setOnCameraIdleListener {
//
//        }
//
//        map?.setOnCameraMoveListener {
////            Log.i("oioioioi",map?.cameraPosition?.target.toString())
////            map?.ci
////            map?.addCircle(CircleOptions().center(map?.cameraPosition?.target).radius(1000.0).fillColor(0x110000FF).strokeColor(0xFF0000FF.toInt()).strokeWidth(1f))
//
//        }
//    }
//
//    private fun setLocation(latLng:LatLng){
//
//        val cameraUpdate = CameraUpdateFactory.newLatLng(latLng)
//        val zoom = CameraUpdateFactory.zoomTo(15f)
//        map?.moveCamera(cameraUpdate)
//        map?.animateCamera(zoom)
//        MapsInitializer.initialize(this)
//
//    }
//
//    private fun drawRadius(lat: Double, lng: Double,radius:Double){
//        val EARTH_RADIUS = 6378100.0 //in meter
//        val bearing = 1.57
//
//        val lat1 = Math.toRadians(lat)
//        val lng1 = Math.toRadians(lng)
//
//        var lat2 = asin(sin(lat1)*cos(radius/EARTH_RADIUS) + cos(lat1)* sin(radius/EARTH_RADIUS)* cos(bearing))
//        var lng2 = lng1 + atan2(sin(bearing)* sin(radius/EARTH_RADIUS)* cos(lat1),
//                                cos(radius/EARTH_RADIUS)- sin(lat1)* sin(lat2))
//
//        lat2 = Math.toDegrees(lat2)
//        lng2 = Math.toDegrees(lng2)
//
//        val p1 = map?.projection?.toScreenLocation(LatLng(lat,lng))
//        val p2 = map?.projection?.toScreenLocation(LatLng(lat2,lng2))
//
//
//        val fill = Paint(Paint.ANTI_ALIAS_FLAG)
//        fill.color = 0x110000FF
//        fill.style = Paint.Style.FILL
//
//        val stroke = Paint(Paint.ANTI_ALIAS_FLAG)
//        stroke.color = 0xFF0000FF.toInt()
//        stroke.style = Paint.Style.STROKE
//
//        val circleSize = abs(p1!!.x-p2!!.x)
//
//        Log.i("oioioioiP1X",p1!!.x.toString())
//        Log.i("oioioioiP2X",p2!!.x.toString())
//        Log.i("oioioioiCS",circleSize.toString())
//
//        val b = Bitmap.createBitmap(circleSize*2,circleSize*2,Bitmap.Config.ARGB_8888)
//        val canvas = Canvas(b)
//        canvas.drawCircle(circleSize.toFloat(),circleSize.toFloat(),circleSize.toFloat(),fill)
//        canvas.drawCircle(circleSize.toFloat(),circleSize.toFloat(),circleSize.toFloat(),stroke)
//
//    }
//
//    private fun onCameraChange(lat:Double,lng:Double){
//
//    }

}

