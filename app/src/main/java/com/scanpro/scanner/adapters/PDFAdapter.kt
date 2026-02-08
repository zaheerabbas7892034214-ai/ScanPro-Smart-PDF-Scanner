package com.scanpro.scanner.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.scanpro.scanner.R
import com.scanpro.scanner.utils.FileManager
import java.io.File

/**
 * Adapter for displaying PDF files in a RecyclerView
 */
class PDFAdapter(
    private var pdfList: List<File>,
    private val onItemClick: (File) -> Unit,
    private val onMenuClick: (File, View) -> Unit
) : RecyclerView.Adapter<PDFAdapter.PDFViewHolder>() {

    class PDFViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgThumbnail: ImageView = itemView.findViewById(R.id.imgThumbnail)
        val tvFileName: TextView = itemView.findViewById(R.id.tvFileName)
        val tvDate: TextView = itemView.findViewById(R.id.tvDate)
        val tvPages: TextView = itemView.findViewById(R.id.tvPages)
        val tvSize: TextView = itemView.findViewById(R.id.tvSize)
        val btnMenu: ImageButton = itemView.findViewById(R.id.btnMenu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pdf, parent, false)
        return PDFViewHolder(view)
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val pdfFile = pdfList[position]
        
        // Set file name
        holder.tvFileName.text = pdfFile.name
        
        // Set date
        holder.tvDate.text = FileManager.formatDate(pdfFile.lastModified())
        
        // Set file size
        holder.tvSize.text = FileManager.formatFileSize(pdfFile.length())
        
        // Set page count (simplified - would need PDF parsing for accurate count)
        // For now, we'll use a placeholder
        holder.tvPages.text = holder.itemView.context.getString(R.string.pages, 1)
        
        // Set click listeners
        holder.itemView.setOnClickListener {
            onItemClick(pdfFile)
        }
        
        holder.btnMenu.setOnClickListener {
            onMenuClick(pdfFile, it)
        }
    }

    override fun getItemCount(): Int = pdfList.size

    /**
     * Updates the PDF list and refreshes the adapter
     */
    fun updateList(newList: List<File>) {
        pdfList = newList
        notifyDataSetChanged()
    }

    /**
     * Removes an item from the list
     */
    fun removeItem(file: File) {
        val position = pdfList.indexOf(file)
        if (position != -1) {
            pdfList = pdfList.toMutableList().apply { removeAt(position) }
            notifyItemRemoved(position)
        }
    }
}
