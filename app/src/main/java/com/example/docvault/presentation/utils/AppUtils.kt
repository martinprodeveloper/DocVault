package com.example.docvault.presentation.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.graphics.scale
import java.io.File

object AppUtils {

    fun addWatermark(
        bitmap: Bitmap,
        text: String?,
        textSize: Float = 36f,
        color: Int = Color.RED,
        alpha: Int = 100,
        xPos: Float = 20f,
        yOffsetFromBottom: Float = 40f
    ): Bitmap {
        val mutableBitmap = toMutable(bitmap)
        val canvas = Canvas(mutableBitmap)
        val paint = Paint().apply {
            this.color = color
            this.alpha = alpha
            this.textSize = textSize
            isAntiAlias = true
        }
        text?.let {
            canvas.drawText(it, xPos, bitmap.height - yOffsetFromBottom, paint)
        }
        return mutableBitmap
    }

    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratio = minOf(
            maxWidth.toFloat() / bitmap.width,
            maxHeight.toFloat() / bitmap.height
        )
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        return bitmap.scale(width, height)
    }

    fun toMutable(bitmap: Bitmap): Bitmap {
        return if (bitmap.isMutable) bitmap
        else bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
    }

    fun openPdf(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, "application/pdf")
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }
}