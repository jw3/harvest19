package com.github.jw3.mvf

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import com.esri.arcgisruntime.layers.RasterLayer
import com.esri.arcgisruntime.mapping.ArcGISMap
import com.esri.arcgisruntime.mapping.Basemap
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.raster.Raster
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val layers = Environment.getExternalStorageDirectory().list()
            .filter { it.endsWith(".tif") }
            .map { Paths.get(Environment.getExternalStorageDirectory().path, it).toString() }
            .map { Raster(it) }
            .map { RasterLayer(it) }

        mapView.map = ArcGISMap(Basemap(layers, emptyList()))
        mapView.map.initialViewpoint = Viewpoint(42.0, 42.0, 4.0)

        mapView.map.addDoneLoadingListener {
            println(mapView.map.spatialReference.wkid)
            println(mapView.map.spatialReference.unit.name)
        }
    }
}
