package com.geopic

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridView
import android.widget.ImageButton
import java.io.File
import java.io.FileInputStream


class ThirdFragment : Fragment(R.layout.fragment_third) {
    private lateinit var imageList: ArrayList<File>
    lateinit var buttonSettings: ImageButton
    lateinit var buttonDelete: ImageButton
    private lateinit var gridView: GridView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_third, container, false)
        buttonDelete = view.findViewById(R.id.buttonDelete)
        buttonSettings = view.findViewById(R.id.buttonSettings)
        buttonDelete.visibility = View.GONE
        gridView = view.findViewById<GridView>(R.id.gridView)
        gridView.numColumns = 3
        gridView.verticalSpacing = 8
        imageList = loadImagesFromInternalStorage()
        val imageAdapter = ImageAdapter(requireContext(), imageList, buttonDelete)
        gridView.adapter = imageAdapter

        buttonDelete.setOnClickListener {
            imageAdapter.deleteSelectedImages()
            buttonDelete.visibility = View.GONE
        }

        buttonSettings.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            context?.startActivity(intent)
        }

        return view
    }

    private fun loadImagesFromInternalStorage(): ArrayList<File> {
        val fileList = ArrayList<File>()
        val path = requireContext().getDir("ImagesDir", Context.MODE_PRIVATE)
        val directory = File(path.toString())

        val files = directory.listFiles()
        files?.sortWith(Comparator { f1, f2 ->
            when {
                f1.lastModified() > f2.lastModified() -> -1
                f1.lastModified() < f2.lastModified() -> 1
                else -> 0
            }
        })

        files?.let {
            for (file in it) {
                fileList.add(file)
            }
        }

        return fileList
    }
}