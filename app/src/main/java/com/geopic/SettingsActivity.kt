package com.geopic

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    lateinit var returnButton: ImageButton
    lateinit var flashLight: Button
    private var isFlashlightOn: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)
        returnButton = findViewById(R.id.returnButton)
        flashLight = findViewById(R.id.flashlightButton)

        flashLight.setOnClickListener {
            val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val cameraID = cameraManager.cameraIdList.firstOrNull() { id ->
                cameraManager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }

            if (cameraID != null) {
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(cameraID,isFlashlightOn)
                if (isFlashlightOn) {
                    Log.d("Flashlight","Flashlight is ON")
                    Toast.makeText(this,"Flashlight is ON",Toast.LENGTH_SHORT)
                } else {
                    Log.d("Flashlight","Flashlight is OFF")
                    Toast.makeText(this,"Flashlight is OFF",Toast.LENGTH_SHORT)
                }
            }
        }

        returnButton.setOnClickListener {
            finish()
        }
    }
}