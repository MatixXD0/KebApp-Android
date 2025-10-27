package pl.kul.kebapp.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class LocationProvider(private val context: Context) {

    companion object {
        val DefaultLocationRequest: LocationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10_000L)
                .setMinUpdateIntervalMillis(5_000L)
                .build()
    }

    private val client: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    //Jednorazowe pobranie ostatniej znanej lokalizacji (może być null!)
    @SuppressLint("MissingPermission")
    suspend fun getLastLocation(): Location? =
        client.lastLocation.await()

    @SuppressLint("MissingPermission")
    fun locationUpdates(request: LocationRequest = DefaultLocationRequest): Flow<Location> =
        callbackFlow {
            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    result.lastLocation?.let { trySend(it) }
                }
            }
            client.requestLocationUpdates(request, callback, Looper.getMainLooper())
            awaitClose { client.removeLocationUpdates(callback) }
        }
}
