package com.haberturm.homeworks.screens

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

class DeleteAlertDialog : DialogFragment() {

    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Важное сообщение!")
                .setMessage("Хотите удалить выбранный элемент?")
                .setPositiveButton("Да") { dialog, id ->
                    dialog.cancel()
                    viewModel.deleteItem()
                }
                .setNegativeButton("Нет") { dialog, id ->
                    dialog.cancel()
                    viewModel.cancelDelete()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}