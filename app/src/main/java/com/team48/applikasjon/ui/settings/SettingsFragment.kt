package com.team48.applikasjon.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.ui.map.MapViewModel


class SettingsFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var rootView: View
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var switchDarkMode: SwitchCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupButtons()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        settingsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SettingsViewModel::class.java)

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupButtons()
    }

    private fun setupButtons() {
        switchDarkMode = rootView.findViewById(R.id.switchDarkMode)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.enableDarkMode()
            } else {
                settingsViewModel.disableDarkMode()
            }
        }
    }
}