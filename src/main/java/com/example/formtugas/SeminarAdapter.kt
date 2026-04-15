package com.example.formtugas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SeminarAdapter(
    private val seminars: List<Seminar>,
    private val onItemClick: (Seminar) -> Unit
) : RecyclerView.Adapter<SeminarAdapter.SeminarViewHolder>() {

    class SeminarViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivSeminarImage: ImageView = view.findViewById(R.id.ivSeminarImage)
        val tvCategory: TextView = view.findViewById(R.id.tvSeminarCategory)
        val tvStatus: TextView = view.findViewById(R.id.tvSeminarStatus)
        val tvTitle: TextView = view.findViewById(R.id.tvSeminarTitle)
        val tvSummary: TextView = view.findViewById(R.id.tvSeminarSummary)
        val tvDate: TextView = view.findViewById(R.id.tvSeminarDate)
        val tvLocation: TextView = view.findViewById(R.id.tvSeminarLocation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeminarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_seminar, parent, false)
        return SeminarViewHolder(view)
    }

    override fun onBindViewHolder(holder: SeminarViewHolder, position: Int) {
        val seminar = seminars[position]
        
        // Bind image
        if (seminar.imageResId != null) {
            holder.ivSeminarImage.setImageResource(seminar.imageResId)
        } else {
            holder.ivSeminarImage.setImageResource(R.drawable.bg_pro_header)
        }

        holder.tvCategory.text = seminar.category
        holder.tvStatus.text = seminar.status
        holder.tvTitle.text = seminar.title
        holder.tvSummary.text = seminar.summary
        holder.tvDate.text = seminar.date
        holder.tvLocation.text = seminar.location

        holder.itemView.setOnClickListener { onItemClick(seminar) }
    }

    override fun getItemCount() = seminars.size
}