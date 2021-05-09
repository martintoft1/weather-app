package com.team48.applikasjon.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.ViewModelFactory


class SettingsFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var rootView: View
    private lateinit var repository: Repository
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchLocation: SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupButtons()
    }

    private fun setupViewModel() {
        settingsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SettingsViewModel::class.java)

        repository = settingsViewModel.repository
    }

    private fun setupButtons() {

        switchDarkMode = rootView.findViewById(R.id.switchDarkMode)
        switchLocation = rootView.findViewById(R.id.switchLocation)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            // TODO: Sette flere darkmodes?
            if (isChecked) {
                (activity as MainActivity).changeMapStyle(settingsViewModel.enableDarkMode(), 1)
            } else {
                (activity as MainActivity).changeMapStyle(settingsViewModel.disableDarkMode(), 0)
            }
        }

        switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                (activity as MainActivity).enableLocation()
            } else {
                (activity as MainActivity).disableLocation()
            }
        }
    }

    // Kalles av MapFragment via MainActivity for å vite om henting av brukerlokasjon tillates
    fun getLocationButtonStatus(): Boolean {
        return switchLocation.isChecked
    }
}