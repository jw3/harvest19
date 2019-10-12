package com.github.jw3.mvf

import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.esri.arcgisruntime.layers.RasterLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.raster.Raster
import kotlinx.android.synthetic.main.fragment_map_view.*


private const val StrIdParam = "strIdParam"

class MapView : Fragment() {
    private var listener: OnMapFragmentInteractionListener? = null
    private var strId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            strId = it.getString(StrIdParam)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val f = inflater.inflate(R.layout.fragment_map_view, container, false)



        f.findViewById<com.esri.arcgisruntime.mapping.view.MapView>(R.id.mapView)?.let {
            val x: Location? = currentLocation()
            val lat = x?.latitude ?: 42.0
            val lon = x?.longitude ?: 42.0

            val map = basemapFromStorage()?.let { ArcGISMap(it) } ?: ArcGISMap(
                Basemap.Type.IMAGERY,
                lat,
                lon,
                18
            )

            it.map = map
            it.isAttributionTextVisible = false

            it.map.basemap.baseLayers.first().addDoneLoadingListener {
                val c = map.basemap.baseLayers.first()
                mapView.setViewpoint(Viewpoint(c.fullExtent.center, 18.0))

//                println(mapView.map.spatialReference.wkid)
//                println(mapView.map.spatialReference.unit.name)
            }
        }
        return f
    }

    private fun currentLocation(): Location? {
        val m = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            m.getProviders(true).firstOrNull()?.let { x -> m.getLastKnownLocation(x) }
        } catch (e: SecurityException) {
            Log.w("gps", "unable to get initial location")
            null
        }
    }

    fun basemapFromStorage(): Basemap? {
        return Environment.getExternalStorageDirectory().listFiles()
            ?.map { f -> f.absolutePath }?.let { f ->
                val layers = f.filter { it.endsWith(".tif") }
                    .map {
                        it
                    }
                    .map { Raster(it) }
                    .map { RasterLayer(it) }

                if (layers.isNotEmpty()) Basemap(layers, emptyList()) else null
            }
    }

    fun idStr(): String? {
        return strId
    }

    fun onButtonPressed() {
        listener?.onFragmentInteraction(this)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnMapFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    companion object {
        @JvmStatic
        fun newInstance(strId: String) =
            MapView().apply {
                arguments = Bundle().apply {
                    putString(StrIdParam, strId)
                }
            }
    }
}
