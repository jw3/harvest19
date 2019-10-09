package com.github.jw3.mvf

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.esri.arcgisruntime.geometry.*
import com.esri.arcgisruntime.layers.RasterLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.raster.Raster
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {

    private fun askGpsPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            + ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                111
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askGpsPermission()

        val x: Location? = currentLocation()
        val lat = x?.latitude ?: 42.0
        val lon = x?.longitude ?: 42.0

        val map = basemapFromStorage()?.let { ArcGISMap(it) } ?: ArcGISMap(
            Basemap.Type.IMAGERY,
            lat,
            lon,
            18
        )

        mapView.map = map
        mapView.map.basemap.baseLayers.first().addDoneLoadingListener {
            val c = map.basemap.baseLayers.first()
            mapView.setViewpoint(Viewpoint(c.fullExtent.center, 18.0))

            println(mapView.map.spatialReference.wkid)
            println(mapView.map.spatialReference.unit.name)
        }
    }

    private fun currentLocation(): Location? {
        val m = applicationContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return try {
            m.getProviders(true).firstOrNull()?.let { x -> m.getLastKnownLocation(x) }
        } catch (e: SecurityException) {
            Log.w("gps", "unable to get initial location")
            null
        }
    }

    companion object {
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
    }
}
