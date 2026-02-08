package com.scanpro.scanner

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.scanpro.scanner.adapters.PDFAdapter
import com.scanpro.scanner.utils.FileManager
import java.io.File

/**
 * Activity for displaying all saved PDF documents in a gallery view
 */
class GalleryActivity : AppCompatActivity() {

    private lateinit var toolbar: MaterialToolbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: LinearLayout

    private lateinit var pdfAdapter: PDFAdapter
    private var pdfList = listOf<File>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        initViews()
        setupToolbar()
        setupRecyclerView()
        loadPDFs()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the list when returning to this activity
        loadPDFs()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        recyclerView = findViewById(R.id.recyclerView)
        emptyState = findViewById(R.id.emptyState)
    }

    private fun setupToolbar() {
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        pdfAdapter = PDFAdapter(
            pdfList = emptyList(),
            onItemClick = { file -> openPDF(file) },
            onMenuClick = { file, view -> showMenu(file, view) }
        )

        recyclerView.apply {
            layoutManager = GridLayoutManager(this@GalleryActivity, 1)
            adapter = pdfAdapter
        }
    }

    private fun loadPDFs() {
        pdfList = FileManager.getAllPDFs(this)
        
        if (pdfList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyState.visibility = View.GONE
            pdfAdapter.updateList(pdfList)
            
            // Update toolbar subtitle with count
            toolbar.subtitle = "${pdfList.size} PDFs"
        }
    }

    private fun openPDF(file: File) {
        val intent = Intent(this, PDFViewerActivity::class.java).apply {
            putExtra("PDF_PATH", file.absolutePath)
        }
        startActivity(intent)
    }

    private fun showMenu(file: File, view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.menu_pdf_item)
        
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_open -> {
                    openPDF(file)
                    true
                }
                R.id.action_share -> {
                    sharePDF(file)
                    true
                }
                R.id.action_delete -> {
                    showDeleteConfirmation(file)
                    true
                }
                else -> false
            }
        }
        
        popupMenu.show()
    }

    private fun sharePDF(file: File) {
        try {
            val pdfGenerator = com.scanpro.scanner.utils.PDFGenerator(this)
            val uri = pdfGenerator.getShareableUri(file)
            
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, file.name)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_pdf)))
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to share PDF", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteConfirmation(file: File) {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.confirm_delete))
            .setMessage(getString(R.string.confirm_delete_message))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deletePDF(file)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun deletePDF(file: File) {
        if (FileManager.deletePDF(file)) {
            Toast.makeText(this, getString(R.string.pdf_deleted), Toast.LENGTH_SHORT).show()
            pdfAdapter.removeItem(file)
            loadPDFs() // Refresh to update empty state if needed
        } else {
            Toast.makeText(this, "Failed to delete PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
