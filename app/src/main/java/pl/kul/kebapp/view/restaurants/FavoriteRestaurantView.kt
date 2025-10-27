import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.view.components.FullscreenLoader
import pl.kul.kebapp.view.restaurants.components.RestaurantCard
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun FavoriteRestaurantsView(
    restaurantsViewModel: RestaurantsViewModel,
    onRestaurantClick: (String) -> Unit
) {
    var allRestaurants by remember { mutableStateOf<List<Restaurant>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val favoriteIds by restaurantsViewModel.favorites.collectAsState()

    LaunchedEffect(Unit) {
        restaurantsViewModel.getAllApprovedRestaurants { loadedRestaurants ->
            allRestaurants = loadedRestaurants
            isLoading = false
        }
    }

    val favoriteRestaurants = remember(allRestaurants, favoriteIds) {
        allRestaurants.filter { it.id in favoriteIds }
    }

    if (isLoading) {
        FullscreenLoader()
    } else {
        if (favoriteRestaurants.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    text = stringResource(R.string.you_haven_t_added_your_favorite_restaurants_yet),
                    style = androidx.compose.material3.MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.primary
                    ),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favoriteRestaurants, key = { it.id }) { restaurant ->
                    RestaurantCard(
                        restaurant = restaurant,
                        onClick = onRestaurantClick
                    )
                }
            }
        }
    }
}
