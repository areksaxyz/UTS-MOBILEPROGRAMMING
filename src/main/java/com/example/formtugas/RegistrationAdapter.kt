package com.example.formtugas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.formtugas.data.Registration

class RegistrationAdapter(
    private val registrations: List<Registration>,
    private val onItemClick: (Registration) -> Unit
) : RecyclerView.Adapter<RegistrationAdapter.RegistrationViewHolder>() {

    class RegistrationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRegSeminarTitle: TextView = view.findViewById(R.id.tvRegSeminarTitle)
        val tvRegDate: TextView = view.findViewById(R.id.tvRegDate)
        val tvRegUserName: TextView = view.findViewById(R.id.tvRegUserName)
        val tvRegStatus: TextView = view.findViewById(R.id.tvRegStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistrationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_registration, parent, false)
        return RegistrationViewHolder(view)
    }

    override fun onBindViewHolder(holder: RegistrationViewHolder, position: Int) {
        val reg = registrations[position]
        holder.tvRegSeminarTitle.text = reg.seminarTitle
        holder.tvRegDate.text = "Daftar pada: ${reg.registrationDate}"
        holder.tvRegUserName.text = "Nama: ${reg.userName}"
        
        holder.itemView.setOnClickListener { onItemClick(reg) }
    }

    override fun getItemCount() = registrations.size
}
