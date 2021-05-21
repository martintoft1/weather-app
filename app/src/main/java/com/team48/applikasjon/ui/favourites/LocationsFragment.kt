package com.team48.applikasjon.ui.favourites

import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
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
import com.team48.applikasjon.ui.favourites.adapters.LocationsAdapter
import com.team48.applikasjon.ui.main.MainActivity
import com.team48.applikasjon.ui.main.ViewModelFactory
import com.team48.applikasjon.utils.Animator


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
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_favourites, container, false)
        //binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
        return rootView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getViewModelFactory()
        setupViewModel()
        setupRecyclerview()
    }

    private fun getViewModelFactory() {
        viewModelFactory = (activity as MainActivity).getViewModelFactory()
    }

    private fun setupViewModel() {
        locationsViewModel = ViewModelProviders.of(
            this,
            viewModelFactory
        ).get(LocationsViewModel::class.java)
    }

    fun moveCamera(cameraPosition: CameraPosition, locationModel: LocationModel) {
        (activity as MainActivity).moveCamera(cameraPosition, locationModel)
    }

    private fun setupRecyclerview() {
        /* Initialize */
        recyclerView = rootView.findViewById(R.id.recyclerView)
        locationsAdapter = LocationsAdapter(mutableListOf(), this)
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = locationsAdapter
            setHasFixedSize(true)   // Improves performance
            itemAnimator = null     // Disable default animations
        }

        /* Get favourite locations from database */
        locationsViewModel.getAllLocations().observe(viewLifecycleOwner, {
            locationsAdapter.setLocations(it)
            locationsViewModel.locationModels = it

            if (it.isEmpty()) {
                rootView.findViewById<TextView>(R.id.tv_no_favourites).visibility = View.VISIBLE
            }
        })

        /* Recyclerview item onSwipe */
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
                // Swipe right: navigate to map
                if (direction == ItemTouchHelper.RIGHT) {
                    val locationModel: LocationModel = locationsViewModel.locationModels[viewHolder.bindingAdapterPosition]
                    val cameraPosition: CameraPosition = locationsViewModel.getCameraPositionFromLocation(
                        viewHolder.bindingAdapterPosition
                    )
                    moveCamera(cameraPosition, locationModel)
                }

                // Swipe left: delete
                else if (direction == ItemTouchHelper.LEFT) {
                    locationsViewModel.deleteLocation(viewHolder.bindingAdapterPosition)
                    //locationsAdapter.notifyItemRemoved(viewHolder.bindingAdapterPosition)
                    unfavouriteCurrent()
                }
                itemTouchHelper?.closeOpened() // close the swiped item
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

            /* Returns maximum swipe threshold (0F -> 1F) */
            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                return 1F
            }
        }
        itemTouchHelper = ItemTouchHelperExtension(itemTouchCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    /* Recyclerview item onClick */
    override fun onLocationClick(position: Int, view: View) {
        val location = locationsViewModel.locationModels[position]
        val expandedView = view.findViewById<LinearLayout>(R.id.location_expanded)
        val arrowView = view.findViewById<ImageView>(R.id.arrow_expand)

        /*TransitionManager.beginDelayedTransition(
            view.findViewById(R.id.cv_location),
            AutoTransition().setDuration(400)
        )*/


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

    fun unfavouriteCurrent() {
        (activity as MainActivity).unfavouriteCurrent()
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "LocationsFragment onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "LocationsFragment onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "LocationsFragment onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "LocationsFragment onStop")
    }
}
