package com.example.app_phone_book.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.app_phone_book.Model.Contact
import com.example.app_phone_book.R

class ContactsRecyclerViewAdapter(var contactsList: ArrayList<Contact>, val onClickListener: OnClickListener) : RecyclerView.Adapter<ContactsRecyclerViewAdapter.ContactsViewHolder>() {

    class ContactsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactImage: ImageView = itemView.findViewById(R.id.contact_image)
        val contactName: TextView = itemView.findViewById(R.id.contact_name)
        val contactEmail: TextView = itemView.findViewById(R.id.contact_email)
        val contactPhone: TextView = itemView.findViewById(R.id.contact_phone)
    }

    class OnClickListener(val clickListener: (contact:Contact) -> Unit) {
        fun onClick(contact: Contact) {
            clickListener(contact)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contacts_recycler_view_layout, parent, false)
        return ContactsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return this.contactsList.size
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, position: Int) {
        val contact = contactsList[position]
        //holder.contactImage.setImageResource(contact.imageId)
        holder.contactName.setText("${contact.name}")
        holder.contactEmail.setText("${contact.email}")
        holder.contactPhone.setText("${contact.phone}")
        holder.itemView.setOnClickListener{
            onClickListener.onClick(contact)
        }
    }
}