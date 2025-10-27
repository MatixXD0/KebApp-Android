package pl.kul.kebapp.view.location

import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import pl.kul.kebapp.R

@Composable
fun GPSOffView(onRetry: () -> Unit) {
    val ctx = LocalContext.current

    FullscreenInfoWithActions(
        message = stringResource(R.string.app_requires_GPS)
    ) {
        Button(onClick = onRetry) {
            Text(stringResource(R.string.turn_on_GPS))
        }
        Text(stringResource(R.string.check_system_settings))
        Button(onClick = {
            ctx.startActivity(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }) {
            Text(stringResource(R.string.System_settings))
        }
    }
}