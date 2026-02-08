package com.scanpro.scanner

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.scanpro.scanner.utils.PDFGenerator
import java.io.File

/**
 * Activity for viewing PDF files
 */
class PDFViewerActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var pdfView: PDFView
    private lateinit var fabShare: ExtendedFloatingActionButton

    private var pdfFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pdf_viewer)

        initViews()
        setupToolbar()
        setupClickListeners()
        loadPDF()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        pdfView = findViewById(R.id.pdfView)
        fabShare = findViewById(R.id.fabShare)
    }

    private fun setupToolbar() {
        val pdfPath = intent.getStringExtra("PDF_PATH")
        if (pdfPath != null) {
            pdfFile = File(pdfPath)
            toolbar.title = pdfFile?.name ?: getString(R.string.pdf_viewer)
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupClickListeners() {
        fabShare.setOnClickListener {
            sharePDF()
        }
    }

    private fun loadPDF() {
        val pdfPath = intent.getStringExtra("PDF_PATH")
        
        if (pdfPath != null) {
            val file = File(pdfPath)
            if (file.exists()) {
                pdfView.fromFile(file)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(false)
                    .password(null)
                    .scrollHandle(null)
                    .enableAntialiasing(true)
                    .spacing(10)
                    .onLoad { nbPages ->
                        // PDF loaded successfully
                    }
                    .onPageError { page, _ ->
                        Toast.makeText(
                            this,
                            "Error loading page $page",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    .load()
            } else {
                Toast.makeText(this, "PDF file not found", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "No PDF path provided", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun sharePDF() {
        pdfFile?.let { file ->
            try {
                val pdfGenerator = PDFGenerator(this)
                val uri = pdfGenerator.getShareableUri(file)
                
                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, file.name)
                    putExtra(Intent.EXTRA_TEXT, "Sharing PDF: ${file.name}")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Failed to share PDF: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
