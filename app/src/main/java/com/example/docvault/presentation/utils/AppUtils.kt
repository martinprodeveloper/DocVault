package com.example.docvault.presentation.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.core.graphics.scale

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
        val result = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            this.color = color
            this.alpha = alpha
            this.textSize = textSize
            isAntiAlias = true
        }
        text?.let {
            canvas.drawText(it, xPos, bitmap.height - yOffsetFromBottom, paint)
        }
        return result
    }

    fun scaleBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratio = minOf(maxWidth.toFloat() / bitmap.width, maxHeight.toFloat() / bitmap.height)
        val width = (bitmap.width * ratio).toInt()
        val height = (bitmap.height * ratio).toInt()
        return bitmap.scale(width, height)
    }

    fun toMutable(bitmap: Bitmap): Bitmap {
        return if (bitmap.isMutable) bitmap
        else bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, true)
    }
}