package com.hedroid.weatherapps

data class WeatherResponse(
    val name: String,
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val rain: Rain? // nullable because it may not always be present
)

data class Main(
    val temp: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)

data class Rain(
    val `1h`: Double?
)

data class Weather(
    val description: String
)

