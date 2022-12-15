package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {

    val RequestPermissionCode = 1
    var mLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    val API: String =  "27e0499ec5835ed6d8793a72b0337188"
    private lateinit var geocoder: Geocoder
    var ciudad: String = ""
    var weather_url1 = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ocultarControles()

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

    }

    fun btnConsultar(view: View) {
        obtenerCoordenadas()


    }

    fun obtenerCoordenadas () {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission()
        } else {


            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    mLocation = location
                    if (location != null) {
                        weather_url1 = "https://api.openweathermap.org/data/2.5/w" +
                                "eather?" + "lat=" + location?.latitude +"&lon="+ location?.longitude + "&appid="+ API
                        val address = geocoder.getFromLocation(location?.latitude,  location?.longitude, 1)
                        ciudad = address[0].locality
                        //Toast.makeText(this,ciudad, Toast.LENGTH_SHORT).show()

                        //MUESTRO CONTROLES
                        txtCiudad.setVisibility(View.VISIBLE)
                        txtObtenerCiudad.setVisibility(View.VISIBLE)
                        txtObtenerCiudad.setText(ciudad)
                        obtenerDatosClima(weather_url1)


                    }
                }

        }
    }

    fun obtenerDatosClima(nombre: String) {
        val queue = Volley.newRequestQueue(this)
        val stringReq = StringRequest(
            Request.Method.GET, nombre,
            Response.Listener<String> { response ->
                val obj = JSONObject(response)
                val arr = obj.getJSONArray("weather")
                val obj2 = arr.getJSONObject(0)

                //Toast.makeText(this,obj2.getString("main"), Toast.LENGTH_SHORT).show()
                txtTituloPronostico.setVisibility(View.VISIBLE)
                txtClima1.setVisibility(View.VISIBLE)
                txtClima1.setText(obj2.getString("main"))

                //Toast.makeText(this,obj2.getString("description"), Toast.LENGTH_SHORT).show()
                txtClima2.setVisibility(View.VISIBLE)
                txtClima2.setText(obj2.getString("description"))},


            Response.ErrorListener { "That didn't work!" })
        queue.add(stringReq)
    }
    private fun requestPermission()
    {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            RequestPermissionCode
        )
    }

    private fun ocultarControles()
    {
        txtCiudad.setVisibility(View.GONE)
        txtObtenerCiudad.setVisibility(View.GONE)
        txtTituloPronostico.setVisibility(View.GONE)
        txtClima1.setVisibility(View.GONE)
        txtClima2.setVisibility(View.GONE)
    }
}
