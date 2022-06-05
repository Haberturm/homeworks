package com.haberturm.homeworks.screens.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.haberturm.homeworks.R
import com.haberturm.homeworks.databinding.FragmentDetailBinding
import com.haberturm.homeworks.screens.SharedViewModel
import com.haberturm.homeworks.util.Util.hideKeyboard
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {

    private val viewModel: SharedViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val isTablet = resources.getBoolean(R.bool.isTablet)
        val binding = FragmentDetailBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        navigationObserver(isTablet)
        onLayoutClicked() //need for hiding keyboard when user click on screen
        return binding.root
    }

    //need for hiding keyboard when user click on screen
    private fun onLayoutClicked(){
        lifecycleScope.launch {
            viewModel.onLayoutClicked
                .onEach { click ->
                    if(click != null){
                        hideKeyboard()
                    }
                }
                .launchIn(this)
            viewModel.onLayoutClickDone()
        }
    }

    private fun navigationObserver(isTablet: Boolean){
        lifecycleScope.launch {
            viewModel.navigateToMain
                .onEach { contacts ->
                    if(contacts != null){
                        hideKeyboard()
                        if (isTablet){
                            Unit        // just a placeholder, need to avoid navigation error
                        }else{
                            findNavController().popBackStack(R.id.nav_graph_xml, true) // to clear backstack
                            findNavController().navigate(R.id.mainFragment)
                        }
                    }
                }
                .launchIn(this)
            viewModel.onNavigationComplete()
        }
    }
}