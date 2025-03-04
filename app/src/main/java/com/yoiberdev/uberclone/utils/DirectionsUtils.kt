package com.yoiberdev.uberclone.utils

import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

fun decodePolyline(encoded: String): List<LatLng> {
    val poly = mutableListOf<LatLng>()
    var index = 0
    val len = encoded.length
    var lat = 0
    var lng = 0

    while (index < len) {
        var b: Int
        var shift = 0
        var result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlat = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lat += dlat

        shift = 0
        result = 0
        do {
            b = encoded[index++].code - 63
            result = result or ((b and 0x1f) shl shift)
            shift += 5
        } while (b >= 0x20)
        val dlng = if ((result and 1) != 0) (result shr 1).inv() else (result shr 1)
        lng += dlng

        val point = LatLng(lat / 1E5, lng / 1E5)
        poly.add(point)
    }
    return poly
}

suspend fun fetchRoute(origin: LatLng, destination: LatLng): List<LatLng> = withContext(Dispatchers.IO) {
    try {
        Log.d("FetchRoute", "Fetching route from $origin to $destination")
        val apiKey = "AIzaSyDnIoT1_nhT_BnEWFeoyWtriA4z2L4tCZc"
        val urlStr = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&mode=driving" +
                "&key=$apiKey"
        Log.d("FetchRoute", "URL: $urlStr")
        val url = URL(urlStr)
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = 10000

        val responseCode = connection.responseCode
        Log.d("FetchRoute", "Response code: $responseCode")
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val responseText = connection.inputStream.bufferedReader().use { it.readText() }
            Log.d("FetchRoute", "Response: $responseText")
            val jsonResponse = JSONObject(responseText)
            val routes = jsonResponse.getJSONArray("routes")
            if (routes.length() == 0) {
                Log.d("FetchRoute", "No routes found")
                emptyList<LatLng>()
            } else {
                // Tomamos la primera ruta y decodificamos su polyline
                val route = routes.getJSONObject(0)
                val overviewPolyline = route.getJSONObject("overview_polyline")
                val encodedPolyline = overviewPolyline.getString("points")
                Log.d("FetchRoute", "Encoded polyline: $encodedPolyline")
                decodePolyline(encodedPolyline)
            }
        } else {
            Log.d("FetchRoute", "HTTP error code: $responseCode")
            emptyList<LatLng>()
        }
    } catch (e: Exception) {
        Log.e("FetchRoute", "Error fetching route", e)
        emptyList<LatLng>()
    }
}