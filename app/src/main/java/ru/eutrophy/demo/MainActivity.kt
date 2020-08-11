package ru.eutrophy.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.logo.Alignment
import com.yandex.mapkit.logo.HorizontalAlignment
import com.yandex.mapkit.logo.VerticalAlignment
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.MapType
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActivity : AppCompatActivity() {

    companion object {
        const val MAP_KEY = "You key"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapKitFactory.setApiKey(MAP_KEY)
        MapKitFactory.initialize(this)
        setContentView(R.layout.activity_main)

        val map = mapview.map
        map.mapType = MapType.VECTOR_MAP

        // Запрещено скрывать лого яндекса
        mapview.map.logo.setAlignment(Alignment(HorizontalAlignment.RIGHT, VerticalAlignment.TOP))

        try {
            map.setMapStyle(style())
        } catch (e: IOException) {
            Log.e("ABAY", "Failed to read customization style", e);
        }

        mapview.map.move(
            CameraPosition(Point(55.751574, 37.573856), 11.0f, 0.0f, 0.0f),
            Animation(Animation.Type.SMOOTH, 0f), null
        )
    }

    @Throws(IOException::class)
    private fun readRawResource(name: String): String? {
        val builder = StringBuilder()
        val resourceIdentifier = resources.getIdentifier(name, "raw", packageName)
        val `is` = resources.openRawResource(resourceIdentifier)
        val reader = BufferedReader(InputStreamReader(`is`))
        try {
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                builder.append(line)
            }
        } catch (ex: IOException) {
            Log.e(
                "ABAY",
                "Cannot read raw resource $name"
            )
            throw ex
        } finally {
            reader.close()
        }
        return builder.toString()
    }


    private fun style(): String {
        return readRawResource("map_style") ?: ""
    }

    /* Передайте события onStart и onStop в MapKitFactory и mapView. Иначе MapKit не сможет
    * отобразить карту и остановить обработку карты, когда Activity с картой становится невидимым для пользователя:*/
    override fun onStop() {
        super.onStop()
        mapview.onStop()
        MapKitFactory.getInstance().onStop()
    }

    override fun onStart() {
        super.onStart()
        mapview.onStart()
        MapKitFactory.getInstance().onStart()
    }
}