package com.team48.applikasjon.ui.map

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.Property
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.layers.FillLayer
import com.mapbox.mapboxsdk.style.layers.LineLayer
import com.mapbox.mapboxsdk.style.layers.Property.LINE_JOIN_ROUND
import com.mapbox.mapboxsdk.style.layers.PropertyFactory
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.sources.TileSet
import com.mapbox.mapboxsdk.style.sources.VectorSource
import com.team48.applikasjon.R
import com.team48.applikasjon.VectorTile


class MapView : Fragment(R.layout.fragment_map_view) {

    var mapView: MapView? = null
    //var camPosition: CameraPosition? = null
    //var camDist: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Mapbox.getInstance(requireContext().applicationContext, getString(R.string.mapbox_access_token))

        val view = inflater.inflate(R.layout.fragment_map_view, container, false)

        mapView = view.findViewById(R.id.mapView)
        mapView?.onCreate(savedInstanceState)
        loadMetdata()

        return view
    }

    private fun loadMetdata() {
        val url = "https://test.openmaps.met.no/in2000/map/services"
        val eks = "https://test.openmaps.met.no/services/air_temperature/20210308T060000/001"

        val eksStr = "{\"bounds\":[-11.780325,52.297284,41.783388,73.863569],\"center\":[6.679688,61.438315,9],\"description\":\"air_temperature/20210308T060000/001.mbtiles\",\"format\":\"pbf\",\"generator\":\"tippecanoe v1.36.0\",\"generator_options\":\"tippecanoe --force -o air_temperature/20210308T060000/001.mbtiles -z9 --drop-densest-as-needed air_temperature/20210308T060000/001.geojson\",\"map\":\"https://test.openmaps.met.no/services/air_temperature/20210308T060000/001/map\",\"maxzoom\":9,\"minzoom\":0,\"name\":\"air_temperature/20210308T060000/001.mbtiles\",\"scheme\":\"xyz\",\"tilejson\":\"2.1.0\",\"tiles\":[\"https://test.openmaps.met.no/services/air_temperature/20210308T060000/001/tiles/{z}/{x}/{y}.pbf\"],\"tilestats\":{\"layerCount\":1,\"layers\":[{\"attributeCount\":4,\"attributes\":[{\"attribute\":\"max\",\"count\":13,\"max\":8.5,\"min\":-21.5,\"type\":\"number\",\"values\":[-1.5,-11.5,-14,-16.5,-19,-21.5,-4,-6.5,-9,1,3.5,6,8.5]},{\"attribute\":\"min\",\"count\":13,\"max\":6,\"min\":-50,\"type\":\"number\",\"values\":[-1.5,-11.5,-14,-16.5,-19,-21.5,-4,-50,-6.5,-9,1,3.5,6]},{\"attribute\":\"time\",\"count\":1,\"type\":\"string\",\"values\":[\"2021-03-08T11:00:00Z\"]},{\"attribute\":\"value\",\"count\":13,\"max\":7.25,\"min\":-22.75,\"type\":\"number\",\"values\":[-0.25,-10.25,-12.75,-15.25,-17.75,-2.75,-20.25,-22.75,-5.25,-7.75,2.25,4.75,7.25]}],\"count\":9235,\"geometry\":\"Polygon\",\"layer\":\"001\"}]},\"type\":\"overlay\",\"vector_layers\":[{\"description\":\"\",\"fields\":{\"max\":\"Number\",\"min\":\"Number\",\"time\":\"String\",\"value\":\"Number\"},\"id\":\"001\",\"maxzoom\":9,\"minzoom\":0}],\"version\":\"2\"}"

        //val founderListType: Type = object : TypeToken<MutableList<VectorTile?>?>() {}.type
        val tile = Gson().fromJson(eksStr, VectorTile::class.java)

        //Log.d("test", "test")


        mapView!!.getMapAsync { map ->
            map.setStyle(Style.OUTDOORS) { style ->

                val tileSet = TileSet(tile.tilejson, tile.tiles.toString())
                val vectorSource = VectorSource("metData", tileSet)
                style.addSource(vectorSource)


                val fillLayer = FillLayer("fillId", "metData")
                fillLayer.setProperties(PropertyFactory.fillColor(Color.GREEN))
                //fillLayer.sourceLayer = tile.name.toString()

                // Add fill layer to map
                // her kommer feilmeldingen
                style.addLayer(fillLayer)

            }


        }
/*
        // koden under er hentet fra et eksempel fra mapbox.com med brukav geojson
        mapView!!.getMapAsync { mapboxMap ->
            mapboxMap.setStyle(Style.LIGHT) { style ->
                style.addSource(
                        VectorSource("terrain-data", "mapbox://mapbox.mapbox-terrain-v2")
                )
                val terrainData = LineLayer("terrain-data", "terrain-data")
                terrainData.sourceLayer = "contour"
                terrainData.setProperties(
                        //lineJoin(Property.LINE_JOIN_ROUND),
                        //lineCap(Property.LINE_CAP_ROUND),
                        lineColor(Color.parseColor("#ff69b4")),
                        lineWidth(1.9f)
                )
                style.addLayer(terrainData)
            }
        }


 */



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