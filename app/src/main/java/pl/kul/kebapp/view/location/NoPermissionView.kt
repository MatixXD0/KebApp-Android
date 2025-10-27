package pl.kul.kebapp.view.location

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import pl.kul.kebapp.R

@Composable
fun NoPermissionView(onRetry: () -> Unit) {
    FullscreenInfoWithActions(
        message = stringResource(R.string.app_requires_location)
    ) {
        Button(onClick = onRetry) {
            Text(stringResource(R.string.enable_permission))
        }
    }
}