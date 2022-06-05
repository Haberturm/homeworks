package com.haberturm.homeworks.screens.main

import android.os.Bundle
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
import com.haberturm.homeworks.databinding.FragmentMainBinding
import com.haberturm.homeworks.databinding.FragmentMainTabletBinding
import com.haberturm.homeworks.screens.SharedViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MainFragment : Fragment(){

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
        if (isTablet){
            binding = FragmentMainTabletBinding.inflate(inflater) //inflate tablet layout
            initBinding(binding)
            val navHostFragment =
                childFragmentManager.findFragmentById(R.id.contactsNavContainer) as NavHostFragment // find nested navigation child nav fragment for tablets
            navigationObserver(navHostFragment.navController, R.id.detailFragment2)
        }else{
            binding = FragmentMainBinding.inflate(inflater)
            initBinding(binding)
            navigationObserver(findNavController(),R.id.detailFragment)
        }
        return binding.root
    }


    private fun navigationObserver(navController: NavController,fragmentId: Int){
        lifecycleScope.launch{
            viewModel.navigateToSelectedContact
                .onEach {
                    if (it!=null){ // if it not null, then we should display selected contact
                        navController.navigate(fragmentId)
                        viewModel.displaySelectedContactComplete()
                    }
                }
                .launchIn(this)
        }
    }


    private fun initBinding(binding: ViewBinding){
        when(binding){
            // need for smartcast binding to desired child type
            is FragmentMainTabletBinding -> {
                binding.lifecycleOwner = this
                binding.viewModel = viewModel
                binding.contactList.adapter = ContactsListAdapter(ContactsListAdapter.OnClickListener{
                    viewModel.displaySelectedContact(it)
                })
            }
            is FragmentMainBinding -> {
                binding.lifecycleOwner = this
                binding.viewModel = viewModel
                binding.contactList.adapter = ContactsListAdapter(ContactsListAdapter.OnClickListener{
                    viewModel.displaySelectedContact(it)
                })
            }
        }
    }
}