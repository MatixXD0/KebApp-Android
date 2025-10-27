package pl.kul.kebapp

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.kul.kebapp.location.LocationPermissionRequest
import pl.kul.kebapp.location.LocationSettingsRequest
import pl.kul.kebapp.viewmodel.LocationViewModel
import pl.kul.kebapp.locale.LocaleProvider
import pl.kul.kebapp.view.location.GPSOffView
import pl.kul.kebapp.view.location.NoPermissionView
import pl.kul.kebapp.viewmodel.LoginFlowController

@Composable
fun AppEntry() {
  val locationVm: LocationViewModel = viewModel()
  val permissionGranted by locationVm.permissionGranted.collectAsState()

  var state by remember { mutableStateOf(StartupState.RequestingPermission) }

  // Obsługa flow: jak zmieni się permissionGranted, automatycznie przejdź dalej
  LaunchedEffect(permissionGranted) {
    if (permissionGranted && state == StartupState.RequestingPermission) {
      state = StartupState.RequestingGps
    }
  }

  when (state) {
    // 1) Odmowa runtime-permission
    StartupState.PermissionDenied -> {
      NoPermissionView {
        state = StartupState.RequestingPermission
      }
    }

    // 2) Prośba o runtime-permission
    StartupState.RequestingPermission -> {
      LocationPermissionRequest(
        onGranted = { locationVm.onPermissionResult(true) },
        onDenied = { state = StartupState.PermissionDenied }
      )
    }

    // 3) Odmowa uruchomienia GPS
    StartupState.GpsDenied -> {
      GPSOffView {
        state = StartupState.RequestingGps
      }
    }

    // 4) Prośba o włączenie GPS
    StartupState.RequestingGps -> {
      LocationSettingsRequest(
        onResolved = { state = StartupState.Ready },
        onDeclined = { state = StartupState.GpsDenied }
      )
    }

    // 5) Gdy wszystko OK — startujemy resztę aplikacji
    StartupState.Ready -> {
      LocaleProvider { onLocaleChange ->
        LoginFlowController(onLocaleChange = onLocaleChange)
      }
    }
  }
}

enum class StartupState {
  RequestingPermission,
  PermissionDenied,
  RequestingGps,
  GpsDenied,
  Ready
}
