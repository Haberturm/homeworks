package com.haberturm.homeworks.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haberturm.homeworks.databinding.ContactItemBinding
import com.haberturm.homeworks.models.Contact

/*
Да, RecyclerView не нужен для этого задания, но почему бы и не потренироваться  :)
 */

class ContactsListAdapter (private val onClickListener: OnClickListener, private val onLongClickListener: OnLongClickListener) :
    ListAdapter<Contact, ContactsListAdapter.ContactViewHolder>(DiffCallback){


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
            return oldItem.id == newItem.id
        }
    }

    val differ = AsyncListDiffer(this, DiffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        return ContactViewHolder(ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val contact = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(position)
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener.onLongClick(position)
            return@setOnLongClickListener true
        }
        holder.bind(contact)
    }

    class OnClickListener(val clickListener: (position:Int) -> Unit ) {
        fun onClick(position: Int) = clickListener(position)

    }

    class OnLongClickListener(val longClickListener: (position: Int) -> Unit){
        fun onLongClick(position: Int) = longClickListener(position)
    }

}
