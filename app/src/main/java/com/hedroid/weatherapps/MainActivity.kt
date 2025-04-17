package com.hedroid.weatherapps

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.hedroid.weatherapps.databinding.ActivityMainBinding
import java.util.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: WeatherViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST = 100
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnSearch.setOnClickListener {
            val city = binding.etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchWeather(city)
            }
        }

        viewModel.weatherData.observe(this) { weather ->
            showLoading(false)
            binding.btnSearch.isEnabled = true

            if (weather != null) {
                binding.tvCity.text = weather.name
                binding.tvTemp.text = "${weather.main.temp.toInt()}°C"
                binding.tvDescription.text =
                    weather.weather[0].description.replaceFirstChar { it.uppercase() }


                binding.tvHumidity.text = "Humidity: ${weather.main.humidity}%"
                val windKmh = (weather.wind.speed * 3.6)
                binding.tvWind.text = "Wind: ${"%.1f".format(windKmh)} km/h"

                val precipitation = weather.rain?.`1h` ?: 0.0
                binding.tvPrecipitation.text = "Precipitation: $precipitation mm"
            } else {
                binding.tvCity.text = "City not found"
                binding.tvTemp.text = ""
                binding.tvDescription.text = ""
                binding.tvHumidity.text = ""
                binding.tvWind.text = ""
                binding.tvPrecipitation.text = ""
            }
        }

        checkLocationPermissionAndLoadWeather()
    }

    private fun fetchWeather(city: String) {
        showLoading(true)
        binding.btnSearch.isEnabled = false
        viewModel.getWeather(city)
    }

    private fun showLoading(isLoading: Boolean) {
        binding.tvLoading.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun checkLocationPermissionAndLoadWeather() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )
        } else {
            loadWeatherFromLocation()
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadWeatherFromLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val geocoder = Geocoder(this, Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                val cityName = addresses?.get(0)?.locality
                if (!cityName.isNullOrEmpty()) {
                    fetchWeather(cityName)
                }
            }
        }
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_REQUEST && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            loadWeatherFromLocation()
        } else {
            // Permission denied, fallback or show message
        }

    }
}


