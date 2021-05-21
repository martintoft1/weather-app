package com.team48.applikasjon.ui.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.ViewModelProviders
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.locations.LocationsViewModel
import com.team48.applikasjon.ui.main.ViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SettingsFragment() : Fragment() {

    // Variabler relatert til views og viewmodels
    private lateinit var rootView: View
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var locationsViewModel: LocationsViewModel

    // Knapper for darkmode, brukerlokasjon og sletting
    private lateinit var switchDarkMode: SwitchCompat
    private lateinit var switchLocation: SwitchCompat
    private lateinit var deleteButton: Button

    // Android Studio Shared Preferences-database
    private lateinit var sharedPref: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false)
        return rootView
    }

    // Oppsett av sentrale funksjonaliteter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModelFactory()
        setupViewModel()
        setupPreferences()
        setupButtons()
    }

    // Henter inn ViewModelFactory for oppsett av ViewModel
    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    // Oppsett av ViewModels
    private fun setupViewModel() {
        settingsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(SettingsViewModel::class.java)

        locationsViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(LocationsViewModel::class.java)
    }

    // Oppsett av knapper og onClickListeners
    private fun setupButtons() {

        switchDarkMode = rootView.findViewById(R.id.switchDarkMode)
        switchLocation = rootView.findViewById(R.id.switchLocation)
        deleteButton   = rootView.findViewById(R.id.delete)

        // Henter lagret tilstand hvis den eksisterer
        switchDarkMode.isChecked = loadPreferences("darkMode")
        switchLocation.isChecked = loadPreferences("locationMode")

        // Darkmode onClickListener
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->

            if (isChecked) {
                (activity as MainActivity).changeMapStyle(settingsViewModel.enableDarkMode(), 1)
            } else {
                (activity as MainActivity).changeMapStyle(settingsViewModel.disableDarkMode(), 0)
            }
        }

        // Brukerlokasjons onClickListener
        switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                (activity as MainActivity).enableLocation()
            } else {
                (activity as MainActivity).disableLocation()
            }
        }

        // Sletteknapp onClickListener
        deleteButton.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                locationsViewModel.clearDatabase()
                unFavouriteCurrent()
                updateNoFavourites(View.VISIBLE)
            }
        }
    }

    // Oppsett av Shared Preferences
    private fun setupPreferences() {
        sharedPref = requireActivity().getPreferences(Context.MODE_PRIVATE)
    }

    // Skriving av variabel til disk
    private fun commitPreference(key: String, value: Boolean) {
        with (sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    // Lesing av variabel fra disk
    private fun loadPreferences(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    // Kalles av MapFragment via MainActivity for å vite om henting av brukerlokasjon tillates
    fun getLocationButtonStatus(): Boolean {
        return switchLocation.isChecked
    }

    // Grensesnitt for å hente status på darkmodeknapp
    fun getDarkModeButtonStatus(): Boolean {
        return switchDarkMode.isChecked
    }

    // Grensesnitt for å toggling av favorittknapp
    fun unFavouriteCurrent() {
        (activity as MainActivity).unfavouriteCurrent()
    }

    // Oppdatering av favoritter
    fun updateNoFavourites(status: Int) {
        (activity as MainActivity).updateNoFavourites(status)
    }

    override fun onStop() {
        super.onStop()
        commitPreference("darkMode", switchDarkMode.isChecked)
        commitPreference("locationMode", switchLocation.isChecked)
    }
}