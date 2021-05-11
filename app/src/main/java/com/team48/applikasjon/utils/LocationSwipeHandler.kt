package com.team48.applikasjon.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.team48.applikasjon.R
import com.team48.applikasjon.ui.favourites.LocationsViewModel

class LocationSwipeHandler(
        private val context: Context,
        private val viewModel: LocationsViewModel)

    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val swipe_limit = 4F
    val ic_remove: Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_remove)
    val ic_pin:    Drawable? = ContextCompat.getDrawable(context, R.drawable.ic_pin)


    /* Ikke i bruk */
    override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder): Boolean {

        return false
    }


    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        // Swipe right: delete
        if (direction == ItemTouchHelper.RIGHT) {
            viewModel.deleteLocation(viewHolder.bindingAdapterPosition)
            Log.d("onSwiped", viewModel.locations.size.toString())
        }

        // Swipe left: navigate to map
        else if (direction == ItemTouchHelper.LEFT) {
            Log.d("onSwiped", "leftswipe")
        }
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                             dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // Get RecyclerView item from the ViewHolder
            val itemView: View = viewHolder.itemView
            val bg_right = ColorDrawable(Color.LTGRAY)
            val bg_left  = ColorDrawable(Color.LTGRAY)
            val backgroundCornerOffset = 20

            /* Swiping to the right */
            if (dX > 0) {
                // Set background for positive displacement
                bg_right.setBounds(itemView.left, itemView.top,
                        itemView.left + dX.toInt() + backgroundCornerOffset,
                        itemView.bottom)
                bg_right.draw(c)

                if (ic_pin != null) { // Set pin icon
                    println("drawing pin")
                    val icMargin = (itemView.height - ic_pin.intrinsicHeight) / 2
                    val icTop    = itemView.top + (itemView.height - ic_pin.intrinsicHeight) / 2
                    val icBtm    = icTop + ic_pin.intrinsicHeight
                    val icLeft   = itemView.left + icMargin + ic_pin.intrinsicWidth
                    val icRight  = itemView.left + icMargin
                    ic_pin.setBounds(icLeft, icTop, icRight, icBtm)
                    ic_pin.draw(c)
                }


            /* Swiping to the left */
            } else if (dX < 0) {
                // Set color for negative displacement
                bg_left.setBounds(itemView.right + dX.toInt() - backgroundCornerOffset,
                        itemView.top, itemView.right, itemView.bottom)
                bg_left.draw(c)

                if(ic_remove != null) { // Set remove icon
                    val icMargin = (itemView.height - ic_remove.intrinsicHeight) / 2
                    val icTop    = itemView.top + (itemView.height - ic_remove.intrinsicHeight) / 2
                    val icBtm    = icTop + ic_remove.intrinsicHeight
                    val icLeft   = itemView.right - icMargin - ic_remove.intrinsicWidth
                    val icRight  = itemView.right - icMargin
                    ic_remove.setBounds(icLeft, icTop, icRight, icBtm)
                    ic_remove.draw(c)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX / swipe_limit, dY, actionState, isCurrentlyActive)
        }
    }
}