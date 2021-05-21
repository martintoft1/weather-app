package com.team48.applikasjon.utils

import android.R.animator
import android.R.style
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.AnimatorUpdateListener
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import com.androidnetworking.AndroidNetworking.cancel
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import io.reactivex.internal.subscriptions.SubscriptionHelper.cancel

/* Inneholder funksjoner for å animere ulike views */
class Animator {

    val DELAY_EXTRA: Float = 200F
    val ROTATE_DUR: Long = 400


    /* Roterer pil gitt antall grader */
    fun rotateArrow(view: View, degrees: Float) {
        view.animate().setDuration(ROTATE_DUR).rotation(degrees).start()
    }

    /* Hjertepump-animasjon */
    fun expandHeart(view: View) {
        val scaleDown: ObjectAnimator = ObjectAnimator.ofPropertyValuesHolder(
                view,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f))
        scaleDown.duration = 310
        scaleDown.repeatCount = 1;
        scaleDown.repeatMode = ObjectAnimator.REVERSE;
        scaleDown.start()
    }

    fun expandItem(view: View) {
        val animation = expandAction(view)
        view.startAnimation(animation)
    }

    /* Utvider lokasjonsview */
    private fun expandAction(view: View): Animation {
        view.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val actualheight: Int = view.measuredHeight + 30
        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) ViewGroup.LayoutParams.WRAP_CONTENT else (actualheight * interpolatedTime).toInt()
                view.requestLayout()
            }
        }
        animation.duration =
                ((actualheight / view.context.resources.displayMetrics.density)+DELAY_EXTRA).toLong()
        view.startAnimation(animation);
        return animation
    }

    /* Kollapser lokasjonsview */
    fun collapseItem(view: View) {
        val actualHeight: Int = view.measuredHeight
        val animation: Animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        actualHeight - (actualHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }
        }
        animation.duration =
            ((actualHeight / view.context.resources.displayMetrics.density)+DELAY_EXTRA).toLong()
        view.startAnimation(animation)
    }
}