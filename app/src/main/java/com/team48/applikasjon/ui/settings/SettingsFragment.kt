package com.team48.applikasjon.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.data.repository.Repository
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.SharedViewModel
import com.team48.applikasjon.ui.main.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SettingsFragment() : Fragment() {

    private lateinit var rootView: View
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchLocation: SwitchCompat
    private lateinit var deleteButton: Button
    private lateinit var sharedPref: SharedPreferences

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
        getViewModelFactory()
        setupViewModel()
        setupPreferences()
        setupButtons()
    }

    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    private fun setupViewModel() {
        settingsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SettingsViewModel::class.java)

        sharedViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SharedViewModel::class.java)
    }

    private fun setupButtons() {

        switchDarkMode = rootView.findViewById(R.id.switchDarkMode)
        switchLocation = rootView.findViewById(R.id.switchLocation)
        deleteButton   = rootView.findViewById(R.id.delete)

        // Henter lagret tilstand hvis den eksisterer
        switchDarkMode.isChecked = loadPreferences("darkMode")
        switchLocation.isChecked = loadPreferences("locationMode")

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

        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                sharedViewModel.clearDatabase()
                unFavouriteCurrent()
                updateNoFavourites(View.VISIBLE)
            }
        }
    }

    private fun setupPreferences() {
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    private fun commitPreference(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    private fun loadPreferences(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    // Kalles av MapFragment via MainActivity for å vite om henting av brukerlokasjon tillates
    fun getLocationButtonStatus(): Boolean {
        return switchLocation.isChecked
    }

    fun getDarkModeButtonStatus(): Boolean {
        return switchDarkMode.isChecked
    }

    fun unFavouriteCurrent() {
        (activity as MainActivity).unfavouriteCurrent()
    }

    fun updateNoFavourites(status: Int) {
        (activity as MainActivity).updateNoFavourites(status)
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "SettingsFragment onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "SettingsFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "SettingsFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        commitPreference("darkMode", switchDarkMode.isChecked)
        commitPreference("locationMode", switchLocation.isChecked)
        Log.d("Lifecycle", "SettingsFragment onStop")
    }
}