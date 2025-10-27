package pl.kul.kebapp.view.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.view.components.FullscreenLoader
import pl.kul.kebapp.view.restaurants.components.DistanceSelector
import pl.kul.kebapp.view.restaurants.components.RestaurantCard
import pl.kul.kebapp.view.restaurants.components.SortDropdown
import pl.kul.kebapp.viewmodel.LocationViewModel
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun RestaurantListView(
    restaurantsViewModel: RestaurantsViewModel,
    locationViewModel: LocationViewModel,
    onRestaurantClick: (String) -> Unit
) {
    // Restauracje i status ładowania
    var restaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    //Filtrowanie po odległości
    val selectedRadius by restaurantsViewModel.selectedRadiusInKm.collectAsState()
    val userLocation by locationViewModel.locationFlow.collectAsState()

    val filteredRestaurants = remember(userLocation, restaurants, selectedRadius) {
        restaurantsViewModel.filterRestaurantsByLocation(restaurants, userLocation)
    }

    //Sortowanie
    val sortOption by restaurantsViewModel.sortOption.collectAsState()

    val filteredAndSortedRestaurants = remember(
        userLocation, restaurants, selectedRadius, sortOption
    ) {
        val filtered = restaurantsViewModel.filterRestaurantsByLocation(restaurants, userLocation)
        restaurantsViewModel.sortRestaurants(filtered, userLocation, sortOption)
    }

    LaunchedEffect(Unit) {
        restaurantsViewModel.getAllApprovedRestaurants { loadedRestaurants ->
            restaurants = loadedRestaurants
            isLoading = false
        }
    }

    if (isLoading) {
        FullscreenLoader()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            DistanceSelector(
                selectedRadius = selectedRadius,
                onRadiusChange = { restaurantsViewModel.setSelectedRadius(it) },
                modifier = Modifier.padding(bottom = 16.dp)
            )
            SortDropdown(
                selectedOption = sortOption,
                onOptionSelected = { restaurantsViewModel.setSortOption(it) },
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                //items(filteredRestaurants) { restaurant ->
                items(filteredAndSortedRestaurants) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = onRestaurantClick
                    )
                }
            }
        }
    }
}
