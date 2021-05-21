package com.team48.applikasjon.ui.locations

import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.LocationModel
import com.team48.applikasjon.ui.locations.adapters.LocationsAdapter
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.utils.Animator

/* UI-fragment for Lokasjons/Favoritt-tabben */
class LocationsFragment() : Fragment(), LocationsAdapter.OnLocationClickListener {

    lateinit var recyclerView: RecyclerView
    private lateinit var rootView: View
    private lateinit var locationsViewModel: LocationsViewModel
    private lateinit var locationsAdapter: LocationsAdapter
    private lateinit var viewModelFactory: ViewModelFactory
    val animator = Animator()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView = inflater.inflate(R.layout.fragment_favourites, container, false)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModelFactory()
        setupViewModel()
        setupRecyclerview()
    }

    // Oppretter felles forbindelse til ViewModelFactory
    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    // Oppretter ViewModel basert på ViewModelFactory
    private fun setupViewModel() {
        locationsViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(LocationsViewModel::class.java)
    }

    // Grensesnitt for bevegelse av kamera i kartfragment
    fun moveCamera(cameraPosition: CameraPosition, locationModel: LocationModel) {
        (activity as MainActivity).moveCamera(cameraPosition, locationModel)
    }

    // Oppsett av RecyclerView
    private fun setupRecyclerview() {
        recyclerView = rootView.findViewById(R.id.recyclerView)
        locationsAdapter = LocationsAdapter(mutableListOf(), this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationsAdapter
            setHasFixedSize(true)   // Forbedrer ytelse
            itemAnimator = null     // Disabler standardanimasjoner
        }

        // Henter favorittlokasjoner fra database
        locationsViewModel.getAllLocations().observe(viewLifecycleOwner, {
            locationsAdapter.setLocations(it)
            locationsViewModel.locationModels = it

            if (it.isEmpty()) {
                rootView.findViewById<TextView>(R.id.tv_no_favourites).visibility = View.VISIBLE
            }
        })

        // Håndtering av swipe-funksjonalitet
        var itemTouchHelper: ItemTouchHelperExtension? = null
        val itemTouchCallback = object : ItemTouchHelperExtension.SimpleCallback(
            0, ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
        ) {

            override fun onMove(
                p0: RecyclerView?,
                p1: RecyclerView.ViewHolder?,
                p2: RecyclerView.ViewHolder?
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                if (viewHolder == null) return

                // Swipe høyre: Navigér til kart
                if (direction == ItemTouchHelper.RIGHT) {
                    val locationModel: LocationModel = locationsViewModel.locationModels[viewHolder.bindingAdapterPosition]
                    val cameraPosition: CameraPosition = locationsViewModel.getCameraPositionFromLocation(
                        viewHolder.bindingAdapterPosition
                    )
                    moveCamera(cameraPosition, locationModel)
                }

                // Swipe venstre: Sletting
                else if (direction == ItemTouchHelper.LEFT) {
                    locationsViewModel.deleteLocation(viewHolder.bindingAdapterPosition)
                    unfavouriteCurrent()
                }
                itemTouchHelper?.closeOpened() // Lukker swiped item
            }

            override fun onChildDraw(
                c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    if (viewHolder is LocationsAdapter.ViewHolder) {
                        viewHolder.swipeableContent.translationX = dX / 4
                    }
                }
            }

            // Setter swipe-threshold
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 1F
            }
        }
        itemTouchHelper = ItemTouchHelperExtension(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /* OnClick relatert til RecyclerView
     * Se Database-fil for informasjon om databaseinstans-variabler */
    override fun onLocationClick(position: Int, view: View) {
        val location = locationsViewModel.locationModels[position]
        val expandedView = view.findViewById<LinearLayout>(R.id.location_expanded)
        val arrowView = view.findViewById<ImageView>(R.id.arrow_expand)

        if (location.expanded) {
            animator.collapseItem(expandedView)
            animator.rotateArrow(arrowView, 0F)
            location.expanded = false
        }
        else {
            animator.expandItem(expandedView)
            animator.rotateArrow(arrowView, 180F)
            location.expanded = true
        }
        locationsAdapter.notifyItemChanged(position)
    }

    // Fjern favoritt
    fun unfavouriteCurrent() {
        (activity as MainActivity).unfavouriteCurrent()
    }
}
