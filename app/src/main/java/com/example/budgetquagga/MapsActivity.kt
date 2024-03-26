package com.example.budgetquagga

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.budgetquagga.database.DatabaseRepository

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_new_budget.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private var activeBudget = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        activeBudget = intent.getStringExtra("activeBudget")!!

        if (activeBudget == ""){
            Log.d("DEB", "TRIGGERED")
            Toast.makeText(this@MapsActivity, "No budget found! Have you forgotten to add one?", Toast.LENGTH_SHORT).show()
            finish()
        }


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (activeBudget == ""){
            Toast.makeText(this@MapsActivity, "No budget found! Have you forgotten to add one?", Toast.LENGTH_SHORT).show()
            finish()
        }

        mMap = googleMap

        var budget = Budget(Date(System.currentTimeMillis()/1000), 10, MoneyAmount(10000, 0), "bruh")

        runBlocking {
            val job = GlobalScope.launch {
                val db = DatabaseRepository(application)
                budget = db.getBudget(activeBudget)
            }

            job.join()
        }


        val locations = budget.getLocations()

        for (i in 0 until locations.size/2) {
            if (locations[i * 2].toDouble() == 0.0 && locations[i * 2 + 1].toDouble() == 0.0) {
                continue
            }

            mMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        locations[i * 2].toDouble(),
                        locations[i * 2 + 1].toDouble()
                    )
                )
            )

            mMap.moveCamera(CameraUpdateFactory.newLatLng(LatLng(locations[i * 2].toDouble(), locations[i * 2 + 1].toDouble())))
        }
    }
}
