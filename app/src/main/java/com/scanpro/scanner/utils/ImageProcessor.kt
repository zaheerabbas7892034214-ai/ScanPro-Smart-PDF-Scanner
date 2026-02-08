package com.scanpro.scanner.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Utility class for image processing operations
 */
class ImageProcessor {

    companion object {
        /**
         * Applies grayscale filter to the bitmap
         */
        suspend fun toGrayscale(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height
            val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(result)
            
            val paint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
            
            canvas.drawBitmap(bitmap, 0f, 0f, paint)
            return@withContext result
        }

        /**
         * Applies black and white (high contrast) filter to the bitmap
         */
        suspend fun toBlackAndWhite(bitmap: Bitmap): Bitmap = withContext(Dispatchers.Default) {
            val width = bitmap.width
            val height = bitmap.height
            val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    val pixel = bitmap.getPixel(x, y)
                    
                    // Get RGB values
                    val red = (pixel shr 16) and 0xFF
                    val green = (pixel shr 8) and 0xFF
                    val blue = pixel and 0xFF
                    
                    // Calculate grayscale value
                    val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()
                    
                    // Apply threshold for black and white
                    val threshold = 128
                    val newPixel = if (gray > threshold) 0xFFFFFFFF.toInt() else 0xFF000000.toInt()
                    
                    result.setPixel(x, y, newPixel)
                }
            }
            
            return@withContext result
        }

        /**
         * Adjusts brightness of the bitmap
         * @param value Brightness value between -255 and 255
         */
        suspend fun adjustBrightness(bitmap: Bitmap, value: Float): Bitmap = 
            withContext(Dispatchers.Default) {
                val width = bitmap.width
                val height = bitmap.height
                val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(result)
                
                val paint = Paint()
                val colorMatrix = ColorMatrix(
                    floatArrayOf(
                        1f, 0f, 0f, 0f, value,
                        0f, 1f, 0f, 0f, value,
                        0f, 0f, 1f, 0f, value,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                return@withContext result
            }

        /**
         * Adjusts contrast of the bitmap
         * @param value Contrast value (1.0 = no change, >1.0 = more contrast, <1.0 = less contrast)
         */
        suspend fun adjustContrast(bitmap: Bitmap, value: Float): Bitmap = 
            withContext(Dispatchers.Default) {
                val width = bitmap.width
                val height = bitmap.height
                val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(result)
                
                val paint = Paint()
                val scale = value
                val translate = (-.5f * scale + .5f) * 255f
                val colorMatrix = ColorMatrix(
                    floatArrayOf(
                        scale, 0f, 0f, 0f, translate,
                        0f, scale, 0f, 0f, translate,
                        0f, 0f, scale, 0f, translate,
                        0f, 0f, 0f, 1f, 0f
                    )
                )
                paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
                
                canvas.drawBitmap(bitmap, 0f, 0f, paint)
                return@withContext result
            }

        /**
         * Rotates the bitmap by the specified degrees
         */
        suspend fun rotate(bitmap: Bitmap, degrees: Float): Bitmap = 
            withContext(Dispatchers.Default) {
                val matrix = android.graphics.Matrix()
                matrix.postRotate(degrees)
                return@withContext Bitmap.createBitmap(
                    bitmap, 0, 0, 
                    bitmap.width, bitmap.height, 
                    matrix, true
                )
            }

        /**
         * Creates a copy of the bitmap
         */
        fun copy(bitmap: Bitmap): Bitmap {
            return bitmap.copy(bitmap.config, true)
        }
    }
}
