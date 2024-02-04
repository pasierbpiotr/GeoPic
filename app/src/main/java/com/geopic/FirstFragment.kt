package com.geopic

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.UUID

class FirstFragment : Fragment(R.layout.fragment_first) {

    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_LOCATION_PERMISSION = 2
    private lateinit var viewModel: SharedViewModel
    private lateinit var imageView: ImageView
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private fun dispatchTakePictureIntent() {
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
        val root = inflater.inflate(R.layout.fragment_first, container, false)
        val buttonCamera: Button = root.findViewById(R.id.camera_button)
        imageView = root.findViewById(R.id.imageView)
        buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
            } else {
                dispatchTakePictureIntent()
            }
        }
        return root
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    dispatchTakePictureIntent()
                } else {
                    Toast.makeText(requireContext(),"Uprawnienia do kamery nie zostały udzielone.",Toast.LENGTH_SHORT)
                }
            }
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                } else {
                    Toast.makeText(requireContext(),"Uprawnienia do lokalizacji nie zostały udzielone",Toast.LENGTH_SHORT)
                }
            }
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            getLastLocation { location ->
                saveImageAndLocation(imageBitmap, LatLng(location.latitude, location.longitude))
            }
            val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
            val lastLatitude = sharedPref.getFloat("lastLatitude",0.0f)
            val lastLongitude = sharedPref.getFloat("lastLongitude",0.0f)
        }
    }

    private fun saveImageAndLocation(bitmap: Bitmap, location: LatLng) {
        val wrapper = ContextWrapper(requireContext())
        var file = wrapper.getDir("ImagesDir",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.png")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val sharedPref = activity?.getSharedPreferences("MainActivity",Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(file.name, "${location.latitude},${location.longitude}")
            apply()
        }
    }

    private fun getLastLocation(callback: (Location) -> Unit) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
            return
        }
        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            if (location != null ) {
                callback(location)
            }
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA),REQUEST_IMAGE_CAPTURE)
        } else {
            dispatchTakePictureIntent()
        }
    }
}