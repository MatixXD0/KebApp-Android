package pl.kul.kebapp.view.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.navigation.Screen
import pl.kul.kebapp.view.restaurants.components.FavoriteIcon
import pl.kul.kebapp.view.restaurants.components.OverallRatingBadge
import pl.kul.kebapp.viewmodel.LocationViewModel
import pl.kul.kebapp.viewmodel.RestaurantsViewModel


@Composable
fun MapView(
    viewModel: RestaurantsViewModel,
    locationViewModel: LocationViewModel,
    navController: NavController
) {
    // Pobranie listy restauracji
    var restaurants by remember { mutableStateOf(emptyList<Restaurant>()) }
    LaunchedEffect(Unit) {
        viewModel.getAllApprovedRestaurants { result ->
            restaurants = result
        }
    }

    //Subskrypcja pozycji użytkownika
    val userLocation by locationViewModel.locationFlow.collectAsState()
    // kopia do lokalnej zmiennej, by umożliwić smart cast
    val userLoc = userLocation

    //Czy już wycentrowaliśmy mapę na użytkowniku?
    var hasCentered by remember { mutableStateOf(false) }

    // Inicjalna pozycja kamery
    val initialLatLng = userLoc
        ?.let { LatLng(it.latitude, it.longitude) }
        ?: LatLng(51.2465, 22.5684)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            initialLatLng,
            if (userLoc != null) 15f else 13f
        )
    }

    //Wycentrowanie tylko raz, gdy pojawi się pierwsza lokalizacja
    LaunchedEffect(userLoc) {
        if (userLoc != null && !hasCentered) {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(userLoc.latitude, userLoc.longitude),
                    15f
                )
            )
            hasCentered = true
        }
    }

    // Stan mapy i zaznaczenia
    var isMapLoaded by remember { mutableStateOf(false) }
    var selectedRestaurant by remember { mutableStateOf<Restaurant?>(null) }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapLoaded = { isMapLoaded = true },
            onMapClick = { selectedRestaurant = null }
        ) {
            //Markery restauracji
            restaurants.forEach { restaurant ->
                if (restaurant.latitude != null && restaurant.longitude != null) {
                    Marker(
                        state = MarkerState(
                            position = LatLng(
                                restaurant.latitude,
                                restaurant.longitude
                            )
                        ),
                        icon = BitmapDescriptorFactory.defaultMarker(
                            when {
                                restaurant.averageRating == null -> BitmapDescriptorFactory.HUE_AZURE
                                restaurant.averageRating > 7.0 -> BitmapDescriptorFactory.HUE_GREEN
                                restaurant.averageRating > 4.0 -> BitmapDescriptorFactory.HUE_YELLOW
                                else -> BitmapDescriptorFactory.HUE_RED
                            }
                        ),
                        title = restaurant.name,
                        snippet = restaurant.city,
                        onClick = {
                            selectedRestaurant = restaurant
                            true
                        }
                    )
                }
            }

            //Marker pozycji użytkownika - trzeba będzie raczej zmienic na coś innego
            if (userLoc != null) {
                val userIcon = BitmapDescriptorFactory.fromResource(R.drawable.user_location)

                Marker(
                    state = MarkerState(LatLng(userLoc.latitude, userLoc.longitude)),
                    icon = userIcon,
                    title = stringResource(R.string.my_localization),
                    snippet = String.format("%.5f, %.5f", userLoc.latitude, userLoc.longitude)
                )
            }
        }

        //podczas ładowania mapy
        if (!isMapLoaded) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }

        //Podgląd wybranej restauracji na dole
        selectedRestaurant?.let { restaurant ->

            val favoriteIds by viewModel.favorites.collectAsState()
            val isFav = favoriteIds.contains(restaurant.id)

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-20).dp)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.background,
                        MaterialTheme.shapes.large
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .shadow(8.dp, shape = MaterialTheme.shapes.large)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = restaurant.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    if (restaurant.averageRating == null) {
                        Text(
                            text = stringResource(R.string.no_reviews_yet),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            maxLines = 1
                        )
                    } else {
                        OverallRatingBadge(overallRating = restaurant.averageRating)
                    }

                    Text(
                        text = restaurant.fullAddress(),
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Button(
                        onClick = {
                            navController.navigate(
                                Screen.RestaurantDetailsScreen.createRoute(restaurant.id)
                            )
                        },
                        modifier = Modifier
                            .height(48.dp)
                            .fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(
                            stringResource(R.string.show_reviews),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    FavoriteIcon(
                        isFavorite = isFav,
                        onToggle = { viewModel.toggleFavorite(restaurant) }
                    )
                }
            }
        }
    }
}
