package com.geopic

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.LatLng
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class SecondFragment : Fragment(R.layout.fragment_second) {
    private var mapFragment: MapView? = null
    private var googleMap: GoogleMap? = null
    private val REQUEST_LOCATION_PERMISSION = 3



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_second, container, false)

        mapFragment = rootView.findViewById(R.id.map)
        mapFragment?.onCreate(savedInstanceState)
        mapFragment?.getMapAsync { map ->
            googleMap = map
            if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),REQUEST_LOCATION_PERMISSION)
                if (ContextCompat.checkSelfPermission(requireContext(),android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_LOCATION_PERMISSION)
                }
            }
            googleMap?.isMyLocationEnabled = true

            val sharedPref =
                activity?.getSharedPreferences("MainActivity", Context.MODE_PRIVATE)
            if (sharedPref != null) {
                val allEntries = sharedPref.all
                for ((key, value) in allEntries) {
                    val locationString = value as? String
                    if (locationString != null) {
                        val parts = locationString.split(",")
                        if (parts.size == 2) {
                            val lat = parts[0].toDoubleOrNull()
                            val lng = parts[1].toDoubleOrNull()
                            if (lat != null && lng != null) {
                                val location = LatLng(lat, lng)
                                val marker = googleMap?.addMarker(
                                    MarkerOptions().position(location).title("Marker")
                                )
                                marker?.tag = "data/data/com.geopic/app_ImagesDir/" + key
                            }
                        }
                    }
                }
            }

            googleMap?.setOnMarkerClickListener { marker ->
                val photoPath = marker.tag as? String
                if (photoPath != null) {
                    val intent = Intent(context, FullScreenImageActivity::class.java)
                    intent.putExtra("image",photoPath)
                    startActivity(intent)
                }
                true
            }
        }

        return rootView
    }

    override fun onResume() {
        super.onResume()
        mapFragment?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment?.onLowMemory()
    }
}