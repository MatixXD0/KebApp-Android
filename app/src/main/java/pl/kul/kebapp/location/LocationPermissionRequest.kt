package pl.kul.kebapp.location

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import pl.kul.kebapp.view.components.FullscreenLoader

@Composable
fun LocationPermissionRequest(
    onGranted: () -> Unit,
    onDenied: () -> Unit
) {
    var checking by remember { mutableStateOf(true) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        checking = false
        if (granted) onGranted() else onDenied()
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    if (checking) {
        FullscreenLoader()
    }
}
