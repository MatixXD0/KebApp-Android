package pl.kul.kebapp.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import pl.kul.kebapp.location.LocationProvider

class LocationViewModel(application: Application) : AndroidViewModel(application) {
  private val provider = LocationProvider(application.applicationContext)

  // stan uprawnienia
  private val _permissionGranted = MutableStateFlow(false)
  val permissionGranted: StateFlow<Boolean> = _permissionGranted.asStateFlow()

  fun onPermissionResult(granted: Boolean) {
    _permissionGranted.value = granted
  }

  // strumień Location: jeśli permissionGranted==true to emitujemy locationUpdates()
  @OptIn(ExperimentalCoroutinesApi::class)
  val locationFlow: StateFlow<Location?> = _permissionGranted
    .flatMapLatest { granted ->
      if (granted) {
        // zaczynamy pobierać ciągłe aktualizacje
        provider.locationUpdates()
        // można filtrować lub mapować, tu zostawiamy pełne Location
      } else {
        // dopóki nie ma zgody, emitujemy null
          flowOf(null)
      }
    }
    // kiedy ViewModel jest aktywny, subskrybujemy przez cały czas; początkowo null
    .stateIn(
      scope = viewModelScope,
      started = SharingStarted.Companion.WhileSubscribed(5_000),
      initialValue = null
    )

  //może prrzyda się w przyszłości
  //(opcjonalnie)
  //jednorazowe pobranie ostatniej znanej lokalizacji
  fun fetchLastLocation(onResult: (Location?) -> Unit) {
    if (_permissionGranted.value) {
      viewModelScope.launch {
        val loc = provider.getLastLocation()
        onResult(loc)
      }
    } else {
      onResult(null)
    }
  }
}

//Przykładowe użycie/ przykładowy widok:

/*@Composable
fun SomeScreen() {
  // 1) Pobieramy instancję
  val locationVm: LocationViewModel = viewModel()

  // 2) Subskrybujemy strumień i reagujemy na zmiany
  val location by locationVm.locationFlow.collectAsState()

  when {
    location == null -> {
      Text("Czekam na lokalizację…")
    }
    else -> {
      Text("Twoja pozycja: ${location.latitude}, ${location.longitude}")
      // Tu w sumie można robić wszystko
    }
  }
}

*/