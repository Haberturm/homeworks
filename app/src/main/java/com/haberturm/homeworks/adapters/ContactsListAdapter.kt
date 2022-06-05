package com.haberturm.homeworks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haberturm.homeworks.databinding.ContactItemBinding
import com.haberturm.homeworks.models.Contact

/*
Да, RecyclerView не нужен для этого задания, но почему бы и не потренироваться  :)
 */

class ContactsListAdapter (private val onClickListener: OnClickListener) :
    ListAdapter<Contact, ContactsListAdapter.ContactViewHolder>(DiffCallback) {


    class ContactViewHolder(private var binding: ContactItemBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(contact: Contact) {
            binding.detail = contact
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(position)
        }
        holder.bind(contact)
    }

    class OnClickListener(val clickListener: (position:Int) -> Unit) {
        fun onClick(position: Int) = clickListener(position)
    }



}
