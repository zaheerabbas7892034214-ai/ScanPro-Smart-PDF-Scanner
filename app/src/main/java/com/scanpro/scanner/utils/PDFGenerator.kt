package com.scanpro.scanner.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Utility class for PDF generation from images
 */
class PDFGenerator(private val context: Context) {

    /**
     * Creates a PDF document from a list of bitmaps
     * @param bitmaps List of bitmap images to include in the PDF
     * @param fileName Optional custom filename (default: auto-generated with timestamp)
     * @return File object of the created PDF, or null if creation failed
     */
    suspend fun createPDF(
        bitmaps: List<Bitmap>,
        fileName: String? = null
    ): File? = withContext(Dispatchers.IO) {
        try {
            if (bitmaps.isEmpty()) {
                return@withContext null
            }

            // Generate filename if not provided
            val pdfFileName = fileName ?: generateFileName()
            
            // Get or create PDF directory
            val pdfDirectory = FileManager.getPDFDirectory(context)
            val pdfFile = File(pdfDirectory, pdfFileName)

            // Create PDF document
            val document = Document(PageSize.A4)
            val writer = PdfWriter.getInstance(document, FileOutputStream(pdfFile))
            document.open()

            // Add each bitmap as a page in the PDF
            bitmaps.forEach { bitmap ->
                // Convert bitmap to byte array
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                val byteArray = stream.toByteArray()

                // Create iText image and scale to fit page
                val image = Image.getInstance(byteArray)
                val pageWidth = document.pageSize.width - 40f
                val pageHeight = document.pageSize.height - 40f
                
                // Scale image to fit within page margins
                image.scaleToFit(pageWidth, pageHeight)
                image.setAbsolutePosition(
                    (document.pageSize.width - image.scaledWidth) / 2,
                    (document.pageSize.height - image.scaledHeight) / 2
                )
                
                document.add(image)
                document.newPage()
            }

            document.close()
            writer.close()

            return@withContext pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }

    /**
     * Generates a unique filename with timestamp
     */
    private fun generateFileName(): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "ScanPro_$timestamp.pdf"
    }

    /**
     * Gets a shareable URI for a PDF file
     */
    fun getShareableUri(file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    companion object {
        const val TAG = "PDFGenerator"
    }
}
