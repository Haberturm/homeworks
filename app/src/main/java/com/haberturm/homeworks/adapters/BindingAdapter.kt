package com.haberturm.homeworks.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haberturm.homeworks.models.Contact

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView,
                     data: List<Contact>?) {
    val adapter = recyclerView.adapter as ContactsListAdapter
    adapter.submitList(data)
}