package com.example.termproject.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

object WeatherUtil {

    data class GridPoint(val x: Int, val y: Int)

    fun convertToGrid(lat: Double, lon: Double): GridPoint {
        val RE = 6371.00877  // Earth radius (km)
        val GRID = 5.0       // Grid spacing (km)
        val SLAT1 = 30.0     // Projection latitude 1 (degrees)
        val SLAT2 = 60.0     // Projection latitude 2 (degrees)
        val OLON = 126.0     // Reference longitude (degrees)
        val OLAT = 38.0      // Reference latitude (degrees)
        val XO = 43          // Reference x-coordinate (GRID)
        val YO = 136         // Reference y-coordinate (GRID)

        val DEGRAD = Math.PI / 180.0
        val re = RE / GRID
        val slat1 = SLAT1 * DEGRAD
        val slat2 = SLAT2 * DEGRAD
        val olon = OLON * DEGRAD
        val olat = OLAT * DEGRAD

        val sn = tan(Math.PI * 0.25 + slat2 * 0.5) / tan(Math.PI * 0.25 + slat1 * 0.5)
        val sn2 = ln(cos(slat1) / cos(slat2)) / ln(sn)
        val sf = tan(Math.PI * 0.25 + slat1 * 0.5)
        val sf2 = sf.pow(sn2) * cos(slat1) / sn2
        val ro = re * sf2 / tan(Math.PI * 0.25 + olat * 0.5).pow(sn2)

        val ra = re * sf2 / tan(Math.PI * 0.25 + lat * DEGRAD * 0.5).pow(sn2)
        var theta = lon * DEGRAD - olon
        if (theta > Math.PI) theta -= 2.0 * Math.PI
        if (theta < -Math.PI) theta += 2.0 * Math.PI
        theta *= sn2

        val x = floor(ra * sin(theta) + XO + 0.5).toInt()
        val y = floor(ro - ra * cos(theta) + YO + 0.5).toInt()

        return GridPoint(x, y)
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(context: Context, callback: (Location?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("LocationDebug", "위치 권한 없음")
            callback(null)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                Log.d("LocationDebug", "Fused location 얻음: ${location.latitude}, ${location.longitude}")
                callback(location)
            } else {
                Log.w("LocationDebug", "Fused location은 null")
                callback(null)
            }
        }.addOnFailureListener {
            Log.e("LocationDebug", "Fused location 에러: ${it.message}")
            callback(null)
        }
    }

}