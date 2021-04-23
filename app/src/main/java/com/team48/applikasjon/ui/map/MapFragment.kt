package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillOpacity
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.data.models.VectorTile
import com.team48.applikasjon.ui.main.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

class MapFragment(val viewModelFactory: ViewModelFactory) : Fragment() {

    private lateinit var mapViewModel: MapViewModel
    var mapView: MapView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mapViewModel = ViewModelProviders.of(
                this,
                viewModelFactory
        ).get(MapViewModel::class.java)

        val rootView = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = rootView.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)




        // Initializing map from MapBox servers
        mapView?.getMapAsync{ map ->

            // Setting camera position over Norway
            map.cameraPosition = mapViewModel.getCamStartPos()

            // Initializing map style
            val customStyle = Style.Builder().fromUri(getString(R.string.mapStyleUri))
            map.setStyle(customStyle) { style ->


                // her må vi vente til apiet er lastet inn
                // TODO: IMPLEMENTERE LIVEDATA FOR REPOSITORY ELLER APISERVICEIMPL
                // runblocking på 5 sekunder er midlertidig løsning
                runBlocking {
                    delay(5000)
                }

                val repository = mapViewModel.repository

                // TODO: Lag UI for å sette dette valget av værtype som skal vises
                var weatherType = 0;
                lateinit var weatherTile: VectorTile

                when (weatherType) {
                    0 -> weatherTile = repository.getAirTemp()[0]
                    1 -> weatherTile = repository.getClouds()[0]
                    2 -> weatherTile = repository.getPrecipitation()[0]
                    3 -> weatherTile = repository.getPressure()[0]
                }


                // Adding source to style

                val tileSet = TileSet(weatherTile.tilejson, weatherTile.tiles?.get(0))


                val vectorSource = VectorSource("weatherData", tileSet)
                style.addSource(vectorSource)

                // Creating and adding a layer
                val fillLayer = FillLayer("airTemp", "weatherData")

                // TODO: Create better presentation based on weather type
                /*
                fillLayer.setProperties(
                    fillColor(Color.BLUE),
                    fillOpacity(0.4f),
                )

                 */
/*
                fillLayer.setProperties(
                    fillOpacity(0.4F),
                    fillColor(
                        interpolate( linear(), zoom(),
                            stop(12, step(get("stroke-width"),
                                color(Color.YELLOW),
                                stop(-20f, color(Color.RED)),
                                stop(0f, color(Color.WHITE)),
                                stop(20f, color(Color.BLUE))
                            ))
                        )
                    )
                )
 */
                fillLayer.setProperties(
                    fillOpacity(0.4F),
                    fillColor(interpolate(
                        linear(), zoom(),
                        stop(-20f, color(Color.RED)),
                        stop(0f, color(Color.WHITE)),
                        stop(20f, color(Color.BLUE))
                    ))
                )
                Log.d("tileset", fillLayer.toString())




                // Spesifiserer hvilke zoomnivåer dataen vil være tilgjengelig
                fillLayer.minZoom = weatherTile.minzoom!!.toFloat()
                fillLayer.maxZoom = weatherTile.maxzoom!!.toFloat()

                fillLayer.sourceLayer = weatherTile.getTileId()

                // Adding layer to style
                style.addLayer(fillLayer)

                //Log.d("minZoom", fillLayer.)


            }
        }
/*
        mapView?.getMapAsync{ map ->

            map.addOnMapClickListener {
                Log.d("latlng", it.toString())
            }
        }

 */



        return rootView
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mapView?.onDestroy()
    }
}