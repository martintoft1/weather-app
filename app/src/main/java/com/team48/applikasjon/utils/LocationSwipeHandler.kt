package com.team48.applikasjon.utils

import android.content.Context
import android.graphics.Canvas
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.team48.applikasjon.data.models.DatabaseLocation
import com.team48.applikasjon.ui.favourites.LocationsFragment
import com.team48.applikasjon.ui.favourites.adapters.LocationsAdapter
import com.team48.applikasjon.ui.main.SharedViewModel

class LocationSwipeHandler(
        private val context: Context,
        private val viewModel: SharedViewModel,
        private val locationsFragment: LocationsFragment)

    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {


    /* Ikke i bruk */
    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {

        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Swipe right: navigate to map
        if (direction == ItemTouchHelper.RIGHT) {
            val location: DatabaseLocation = viewModel.databaseLocations[viewHolder.bindingAdapterPosition]
            val cameraPosition: CameraPosition = viewModel.getCameraPositionFromLocation(viewHolder.bindingAdapterPosition)
            locationsFragment.moveCamera(cameraPosition, location.latLong)

            // TODO: Fikse swipe reset
        }

        // Swipe left: delete
        else if (direction == ItemTouchHelper.LEFT) {
            viewModel.deleteLocation(viewHolder.bindingAdapterPosition)
            locationsFragment.unfavouriteCurrent()
        }
    }


    /* Override function to move layout on swipe */
    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

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