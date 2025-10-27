package pl.kul.kebapp.location

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import pl.kul.kebapp.R
import pl.kul.kebapp.view.components.FullscreenLoader

@Composable
fun LocationSettingsRequest(
    onResolved: () -> Unit,
    onDeclined: () -> Unit
) {
    val context = LocalContext.current

    // 1) Definicja żądania lokalizacji
    val locationRequest = LocationProvider.DefaultLocationRequest

    val settingsRequest = LocationSettingsRequest.Builder()
        .addLocationRequest(locationRequest)
        .setAlwaysShow(true)  // wymusza pokazanie okna
        .build()

    // 2) Launcher do systemowego okna „włącz GPS”
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onResolved()
        } else {
            onDeclined()
        }
    }

    // 3) Stany composable
    var checking by remember { mutableStateOf(true) }
    var resolvableException by remember { mutableStateOf<ResolvableApiException?>(null) }

    // 4) Sprawdzamy stan ustawień GPS
    LaunchedEffect(Unit) {
        val client = LocationServices.getSettingsClient(context)
        client.checkLocationSettings(settingsRequest)
            .addOnSuccessListener {
                checking = false
                onResolved()
            }
            .addOnFailureListener { e ->
                checking = false
                if (e is ResolvableApiException) {
                    // trzeba wyświetlić dialog
                    resolvableException = e
                } else {
                    onDeclined()
                }
            }
    }

    // 5) Renderowanie UI zależnie od stanu
    when {
        checking -> FullscreenLoader()

        resolvableException != null -> {
            // GPS wyłączony – przycisk do otwarcia dialogu
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(stringResource(R.string.app_requires_GPS))
                    Button(onClick = {
                        val intent =
                            IntentSenderRequest.Builder(resolvableException!!.resolution).build()
                        launcher.launch(intent)
                    }) {
                        Text(stringResource(R.string.turn_on_GPS))
                    }
                }
            }
        }

        else -> {
            // Nic
            //onResolved() został wywołany
        }
    }
}
