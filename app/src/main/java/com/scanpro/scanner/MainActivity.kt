package com.scanpro.scanner

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import com.scanpro.scanner.adapters.PDFAdapter
import com.scanpro.scanner.utils.FileManager
import java.io.File

/**
 * Main activity - Landing screen with options to scan or view saved PDFs
 */
class MainActivity : AppCompatActivity() {

    private lateinit var cardScan: MaterialCardView
    private lateinit var cardGallery: MaterialCardView
    private lateinit var fabScan: FloatingActionButton
    private lateinit var recyclerViewRecent: RecyclerView
    private lateinit var emptyState: View
    
    private lateinit var pdfAdapter: PDFAdapter
    
    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
        private const val STORAGE_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        loadRecentPDFs()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when returning to this activity
        loadRecentPDFs()
    }

    private fun initViews() {
        cardScan = findViewById(R.id.cardScan)
        cardGallery = findViewById(R.id.cardGallery)
        fabScan = findViewById(R.id.fabScan)
        recyclerViewRecent = findViewById(R.id.recyclerViewRecent)
        emptyState = findViewById(R.id.emptyState)
    }

    private fun setupRecyclerView() {
        pdfAdapter = PDFAdapter(
            pdfList = emptyList(),
            onItemClick = { file -> openPDFViewer(file) },
            onMenuClick = { file, _ -> openPDFViewer(file) }
        )
        
        recyclerViewRecent.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = pdfAdapter
        }
    }

    private fun setupClickListeners() {
        cardScan.setOnClickListener {
            checkPermissionsAndOpenCamera()
        }

        fabScan.setOnClickListener {
            checkPermissionsAndOpenCamera()
        }

        cardGallery.setOnClickListener {
            openGallery()
        }
    }

    private fun loadRecentPDFs() {
        val allPDFs = FileManager.getAllPDFs(this)
        val recentPDFs = allPDFs.take(3) // Show only 3 most recent
        
        if (recentPDFs.isEmpty()) {
            recyclerViewRecent.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerViewRecent.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            pdfAdapter.updateList(recentPDFs)
        }
    }

    private fun checkPermissionsAndOpenCamera() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)
        
        // Add storage permissions based on Android version
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }

        if (missingPermissions.isEmpty()) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                missingPermissions.toTypedArray(),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
    }

    private fun openGallery() {
        val intent = Intent(this, GalleryActivity::class.java)
        startActivity(intent)
    }

    private fun openPDFViewer(file: File) {
        val intent = Intent(this, PDFViewerActivity::class.java).apply {
            putExtra("PDF_PATH", file.absolutePath)
        }
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    openCamera()
                }
            }
        }
    }
}
