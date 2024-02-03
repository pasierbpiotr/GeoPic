package com.geopic

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ImageView
import java.io.File
import java.io.FileInputStream

class ImageAdapter(private val context: Context, private val imageList: ArrayList<File>, private val buttonDelete: Button) : BaseAdapter() {
    override fun getCount(): Int {
        return imageList.size
    }

    override fun getItem(position: Int): Any {
        return imageList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private val screenWidth: Int
    private val selectedImages = ArrayList<Int>()
    private var isSelectionMode = false

    init {
        val displayMetrics = context.resources.displayMetrics
        screenWidth = displayMetrics.widthPixels
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val imageView: ImageView
        if (convertView == null) {
            imageView = ImageView(context)
            imageView.layoutParams = ViewGroup.LayoutParams(screenWidth/3, 500)
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            imageView.setPadding(8, 8, 8, 8)
        } else {
            imageView = convertView as ImageView
        }

        val bitmap = BitmapFactory.decodeStream(FileInputStream(imageList[position]))
        imageView.setImageBitmap(bitmap)

        imageView.setBackgroundColor(if (selectedImages.contains(position)) Color.RED else Color.TRANSPARENT)

//        imageView.setOnClickListener {
//            val intent = Intent(context, FullScreenImageActivity::class.java)
//            intent.putExtra("imagePath", imageList[position].path)
//            context.startActivity(intent)
//        }

        imageView.setOnLongClickListener {
            if (!isSelectionMode)  {
                isSelectionMode = true
                (context as Activity).runOnUiThread { buttonDelete.visibility = View.VISIBLE }
            }
            toggleSelection(it,position)
            true
        }

        imageView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(it,position)
            }
        }
        return imageView
    }

    private fun toggleSelection(view: View, position: Int) {
        if(selectedImages.contains(position)) {
            selectedImages.remove(position)
            view.setBackgroundColor(Color.TRANSPARENT)
        } else {
            selectedImages.add(position)
            view.setBackgroundColor(Color.RED)
        }

        if (selectedImages.isEmpty()) {
            isSelectionMode = false
            (context as Activity).runOnUiThread { buttonDelete.visibility = View.GONE }
        }
        else if (!isSelectionMode) {
            isSelectionMode = true

        }
    }



    fun deleteSelectedImages() {
        selectedImages.sortDescending()
        val path = context.getDir("ImagesDIR", Context.MODE_PRIVATE)
        val directory = File(path.toString())
        val files = directory.listFiles()
        for(position in selectedImages) {
            val file = imageList[position]
            file.delete()
            imageList.removeAt(position)
        }
        selectedImages.clear()
        notifyDataSetChanged()
        isSelectionMode = false
    }
}