package com.scanpro.scanner.utils

import android.content.Context
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for file management operations
 */
class FileManager {

    companion object {
        private const val PDF_DIRECTORY_NAME = "ScanPro"

        /**
         * Gets or creates the PDF storage directory
         */
        fun getPDFDirectory(context: Context): File {
            // Use app-specific external storage directory
            val directory = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                PDF_DIRECTORY_NAME
            )
            
            if (!directory.exists()) {
                directory.mkdirs()
            }
            
            return directory
        }

        /**
         * Gets all PDF files from the storage directory
         */
        fun getAllPDFs(context: Context): List<File> {
            val directory = getPDFDirectory(context)
            val files = directory.listFiles { file ->
                file.isFile && file.extension.equals("pdf", ignoreCase = true)
            }
            return files?.toList()?.sortedByDescending { it.lastModified() } ?: emptyList()
        }

        /**
         * Deletes a PDF file
         */
        fun deletePDF(file: File): Boolean {
            return if (file.exists() && file.isFile) {
                file.delete()
            } else {
                false
            }
        }

        /**
         * Formats file size in human-readable format
         */
        fun formatFileSize(sizeInBytes: Long): String {
            val kb = 1024.0
            val mb = kb * 1024
            val gb = mb * 1024

            return when {
                sizeInBytes >= gb -> String.format("%.2f GB", sizeInBytes / gb)
                sizeInBytes >= mb -> String.format("%.2f MB", sizeInBytes / mb)
                sizeInBytes >= kb -> String.format("%.2f KB", sizeInBytes / kb)
                else -> "$sizeInBytes B"
            }
        }

        /**
         * Formats date in human-readable format
         */
        fun formatDate(timestamp: Long): String {
            val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
            return sdf.format(Date(timestamp))
        }

        /**
         * Creates a temporary image file for camera capture
         */
        fun createTempImageFile(context: Context): File {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_${timestamp}_"
            val storageDir = context.cacheDir
            return File.createTempFile(imageFileName, ".jpg", storageDir)
        }

        /**
         * Clears temporary files from cache
         */
        fun clearTempFiles(context: Context) {
            context.cacheDir.listFiles()?.forEach { file ->
                if (file.name.startsWith("JPEG_") && file.extension == "jpg") {
                    file.delete()
                }
            }
        }

        /**
         * Gets the total storage used by PDFs
         */
        fun getTotalStorageUsed(context: Context): Long {
            val pdfs = getAllPDFs(context)
            return pdfs.sumOf { it.length() }
        }
    }
}
