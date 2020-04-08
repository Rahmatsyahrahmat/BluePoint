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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==REQUEST_LOCATION_PERMISSION){
            presenter.requestLocation(this)
        }
    }

}

