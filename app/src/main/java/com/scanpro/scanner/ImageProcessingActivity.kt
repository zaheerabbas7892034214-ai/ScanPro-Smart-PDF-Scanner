package com.scanpro.scanner

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.scanpro.scanner.utils.FileManager
import com.scanpro.scanner.utils.ImageProcessor
import com.scanpro.scanner.utils.PDFGenerator
import kotlinx.coroutines.launch
import java.io.File

/**
 * Activity for previewing and editing captured images before PDF creation
 */
class ImageProcessingActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var imageView: ImageView
    private lateinit var chipOriginal: Chip
    private lateinit var chipGrayscale: Chip
    private lateinit var chipBW: Chip
    private lateinit var btnAddPage: MaterialButton
    private lateinit var btnCreatePDF: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var originalBitmap: Bitmap? = null
    private var currentBitmap: Bitmap? = null
    private val capturedImages = mutableListOf<Bitmap>()
    
    private var imagePath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_processing)

        initViews()
        setupToolbar()
        setupClickListeners()
        loadImage()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        imageView = findViewById(R.id.imageView)
        chipOriginal = findViewById(R.id.chipOriginal)
        chipGrayscale = findViewById(R.id.chipGrayscale)
        chipBW = findViewById(R.id.chipBW)
        btnAddPage = findViewById(R.id.btnAddPage)
        btnCreatePDF = findViewById(R.id.btnCreatePDF)
        progressBar = findViewById(R.id.progressBar)

        chipOriginal.isChecked = true
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        chipOriginal.setOnClickListener {
            applyOriginalFilter()
        }

        chipGrayscale.setOnClickListener {
            applyGrayscaleFilter()
        }

        chipBW.setOnClickListener {
            applyBlackAndWhiteFilter()
        }

        btnAddPage.setOnClickListener {
            addCurrentPage()
        }

        btnCreatePDF.setOnClickListener {
            createPDF()
        }
    }

    private fun loadImage() {
        imagePath = intent.getStringExtra("IMAGE_PATH")
        
        if (imagePath != null) {
            val file = File(imagePath!!)
            if (file.exists()) {
                originalBitmap = BitmapFactory.decodeFile(file.absolutePath)
                currentBitmap = originalBitmap
                imageView.setImageBitmap(currentBitmap)
            } else {
                Toast.makeText(this, "Image file not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No image path provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun applyOriginalFilter() {
        currentBitmap = originalBitmap
        imageView.setImageBitmap(currentBitmap)
    }

    private fun applyGrayscaleFilter() {
        lifecycleScope.launch {
            showLoading(true)
            originalBitmap?.let { bitmap ->
                currentBitmap = ImageProcessor.toGrayscale(bitmap)
                imageView.setImageBitmap(currentBitmap)
            }
            showLoading(false)
        }
    }

    private fun applyBlackAndWhiteFilter() {
        lifecycleScope.launch {
            showLoading(true)
            originalBitmap?.let { bitmap ->
                currentBitmap = ImageProcessor.toBlackAndWhite(bitmap)
                imageView.setImageBitmap(currentBitmap)
            }
            showLoading(false)
        }
    }

    private fun addCurrentPage() {
        currentBitmap?.let { bitmap ->
            capturedImages.add(ImageProcessor.copy(bitmap))
            Toast.makeText(
                this,
                "Page added (${capturedImages.size} pages)",
                Toast.LENGTH_SHORT
            ).show()
            
            // Return to camera for next page
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun createPDF() {
        currentBitmap?.let { bitmap ->
            // Add current image to list
            capturedImages.add(ImageProcessor.copy(bitmap))
            
            lifecycleScope.launch {
                showLoading(true)
                
                val pdfGenerator = PDFGenerator(this@ImageProcessingActivity)
                val pdfFile = pdfGenerator.createPDF(capturedImages)
                
                showLoading(false)
                
                if (pdfFile != null) {
                    Toast.makeText(
                        this@ImageProcessingActivity,
                        getString(R.string.pdf_created_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    
                    // Clean up temp files
                    FileManager.clearTempFiles(this@ImageProcessingActivity)
                    
                    // Navigate to PDF viewer
                    val intent = Intent(this@ImageProcessingActivity, PDFViewerActivity::class.java)
                    intent.putExtra("PDF_PATH", pdfFile.absolutePath)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this@ImageProcessingActivity,
                        getString(R.string.error_creating_pdf),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release bitmap resources
        originalBitmap?.recycle()
        currentBitmap?.recycle()
        capturedImages.forEach { it.recycle() }
    }
}
