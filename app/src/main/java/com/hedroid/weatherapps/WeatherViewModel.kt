package com.hedroid.weatherapps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherViewModel : ViewModel() {

    private val _weatherData = MutableLiveData<WeatherResponse>()
    val weatherData: LiveData<WeatherResponse> = _weatherData

    private val apiKey = "a94bb962bf478222bfc3c6f1f98f87cd"

    fun getWeather(city: String) {
        RetrofitInstance.api.getWeather(city, "metric", apiKey)
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    if (response.isSuccessful) {
                        _weatherData.postValue(response.body())
                    } else {
                        Log.e("WeatherError", "Response error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    Log.e("WeatherError", t.message ?: "Unknown error")
                }
            })
    }
}
