package com.yoiberdev.uberclone.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import com.yoiberdev.uberclone.BuildConfig

suspend fun fetchDirectionsRoute(origin: LatLng, destination: LatLng): List<LatLng> = withContext(Dispatchers.IO) {
    try {
        Log.d("FetchDirections", "Fetching directions from $origin to $destination")
        // Usa tu API key; se recomienda usar BuildConfig.MAPS_API_KEY para mayor seguridad
        val apiKey = "AIzaSyCr5jCPJfqndnO58MpwjlCFQuFLxQAMbqs"
        // Construir la URL usando las coordenadas
        val urlStr = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&key=$apiKey"
        Log.d("FetchDirections", "URL: $urlStr")

        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 15000
        connection.readTimeout = 15000

        val responseCode = connection.responseCode
        Log.d("FetchDirections", "Response code: $responseCode")
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("FetchDirections", "Response text: $responseText")
            val jsonResponse = JSONObject(responseText)
            val routes = jsonResponse.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.d("FetchDirections", "No routes found")
                emptyList<LatLng>()
            } else {
                // Tomamos la primera ruta
                val route = routes.getJSONObject(0)
                val overviewPolyline = route.getJSONObject("overview_polyline")
                val encodedPolyline = overviewPolyline.getString("points")
                Log.d("FetchDirections", "Encoded polyline: $encodedPolyline")
                decodePolyline(encodedPolyline)
            }
        } else {
            Log.d("FetchDirections", "HTTP error code: $responseCode")
            emptyList<LatLng>()
        }
    } catch (e: Exception) {
        Log.e("FetchDirections", "Error fetching directions", e)
        emptyList<LatLng>()
    }
}
