package com.haberturm.homeworks.screens.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.haberturm.homeworks.R
import com.haberturm.homeworks.adapters.ContactsListAdapter
import com.haberturm.homeworks.adapters.MarginDecorator
import com.haberturm.homeworks.databinding.FragmentMainBinding
import com.haberturm.homeworks.databinding.FragmentMainTabletBinding
import com.haberturm.homeworks.screens.DeleteAlertDialog
import com.haberturm.homeworks.screens.SharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch


class MainFragment : Fragment() {

    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        lateinit var binding: ViewBinding
        val isTablet = resources.getBoolean(R.bool.isTablet)
        if (isTablet) {
            binding = FragmentMainTabletBinding.inflate(inflater) //inflate tablet layout
            initBinding(binding)
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.contactsNavContainer) as NavHostFragment // find nested navigation child nav fragment for tablets
            navigationObserver(navHostFragment.navController, R.id.detailFragment2)
        } else {
            binding = FragmentMainBinding.inflate(inflater)
            initBinding(binding)
            navigationObserver(findNavController(), R.id.detailFragment)
        }
        onLongClickListener()
        return binding.root
    }


    private fun navigationObserver(navController: NavController, fragmentId: Int) {
        lifecycleScope.launch {
            viewModel.navigateToSelectedContact
                .onEach {
                    if (it != null) { // if it not null, then we should display selected contact
                        navController.navigate(fragmentId)
                        viewModel.displaySelectedContactComplete()
                    }
                }
                .launchIn(this)
            // to null all properties. if it will be not null it may lead to errors
            viewModel.onNavigationComplete()


        }
    }


    private fun initBinding(binding: ViewBinding) {
        when (binding) {
            // need for smartcast binding to desired child type
            is FragmentMainTabletBinding -> {  //fragment for tablets
                binding.lifecycleOwner = this
                binding.viewModel = viewModel
                binding.contactList.adapter = ContactsListAdapter(
                    ContactsListAdapter.OnClickListener {
                        viewModel.displaySelectedContact(it)
                    },
                    ContactsListAdapter.OnLongClickListener {
                        viewModel.onLongClick(it)
                    }
                )


            }
            is FragmentMainBinding -> {  //fragment for average smartphones
                binding.lifecycleOwner = this
                binding.viewModel = viewModel
                binding.contactList.adapter = ContactsListAdapter(
                    ContactsListAdapter.OnClickListener {
                        viewModel.displaySelectedContact(it)
                    },
                    ContactsListAdapter.OnLongClickListener {
                        viewModel.onLongClick(it)
                    }
                )
                binding.searchView.requestFocusFromTouch()
                queryTextListener(
                    binding.searchView,
                    fun(query: String) = viewModel.onSearchQueryChanged(query))     //search implementation
                onSearchClickedListener(binding.searchView)
                binding.contactList.itemAnimator = null
                val adapter = binding.contactList.adapter as ContactsListAdapter
                deleteObserver(adapter)                                                         //deleting item with long click
                binding.contactList.addItemDecoration(
                    MarginDecorator(resources.getDimensionPixelSize(R.dimen.default_padding))    //margin with item decoration
                )
            }
        }
    }

    private fun queryTextListener(
        searchView: androidx.appcompat.widget.SearchView,
        onQueryTextChanged: (String) -> Unit
    ) {
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {

            override fun onQueryTextChange(newText: String): Boolean {
                onQueryTextChanged(newText)
                return false
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                onQueryTextChanged(query)
                return false
            }

        })
    }


    private fun onSearchClickedListener(searchView: androidx.appcompat.widget.SearchView) {
        lifecycleScope.launch {
            viewModel.onSearchClicked
                .onEach {
                    if (it != null) {
                        searchView.requestFocus()
                        searchView.onActionViewExpanded()
                    }
                    viewModel.onSearchClickDone()
                }
                .launchIn(this)

        }
    }


    private fun deleteObserver(adapter: ContactsListAdapter) {
        lifecycleScope.launch {
            viewModel.updatedListAfterDelete
                .onEach {
                    if (it != null) {
                        adapter.differ.submitList(it)
                        viewModel.deleteItemDone()
                    }

                }
                .launchIn(this)
        }
    }

    private fun onLongClickListener() {
        lifecycleScope.launch {
            viewModel.displayDeleteAlert
                .onEach {
                    if (it != null) {
                        val myDialogFragment = DeleteAlertDialog()
                        val manager = activity?.supportFragmentManager
                        if (manager != null) {
                            myDialogFragment.show(manager, "deleteAlert")
                        }
                        viewModel.onLongClickDone()
                    }
                }
                .launchIn(this)
        }
    }
}