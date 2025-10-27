package pl.kul.kebapp.view.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.view.restaurants.components.ExpandableSection
import pl.kul.kebapp.view.restaurants.components.ReviewCard
import pl.kul.kebapp.view.restaurants.components.SummarySection
import pl.kul.kebapp.viewmodel.AuthViewModel
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun RestaurantDetailsView(
    restaurantId: String,
    restaurantsViewModel: RestaurantsViewModel,
    authViewModel: AuthViewModel,
    onAddReviewClick: () -> Unit
) {
    val isLoading by restaurantsViewModel.isLoading.collectAsState()
    val reviews by restaurantsViewModel.reviews.collectAsState()
    val reviewStats by restaurantsViewModel.reviewStats.collectAsState()

    var isSummaryExpanded by remember { mutableStateOf(true) }
    var isReviewsExpanded by remember { mutableStateOf(false) }

    var restaurant by remember { mutableStateOf<Restaurant?>(null) }

    val userRole by authViewModel.userRole.collectAsState()

    LaunchedEffect(restaurantId) {
        restaurantsViewModel.getRestaurantById(restaurantId) { loadedRestaurant ->
            restaurant = loadedRestaurant
        }
        restaurantsViewModel.loadRestaurantDetails(restaurantId)
    }

    val favoriteIds by restaurantsViewModel.favorites.collectAsState()
    val isFav = restaurant?.id?.let { favoriteIds.contains(it) } ?: false

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = restaurant?.name ?: "...",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )

                ExpandableSection(
                    title = stringResource(R.string.review_summary),
                    expanded = isSummaryExpanded,
                    onToggle = { isSummaryExpanded = !isSummaryExpanded }
                ) {
                    SummarySection(
                        avgTaste = reviewStats.avgTaste,
                        avgQuality = reviewStats.avgQuality,
                        avgVenue = reviewStats.avgVenue,
                        avgValueForMoney = reviewStats.avgValueForMoney,
                        avgOverall = reviewStats.avgOverall,
                        repeatPercentage = reviewStats.repeatPercentage,
                        isFavorite = isFav,
                        onToggleFavorite = {
                            restaurant?.let { restaurantsViewModel.toggleFavorite(it) }
                        }
                    )
                }

                Spacer(Modifier.height(16.dp))

                ExpandableSection(
                    title = stringResource(R.string.all_reviews),
                    expanded = isReviewsExpanded,
                    onToggle = { isReviewsExpanded = !isReviewsExpanded }
                ) {
                    if (reviews.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_reviews_yet),
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(8.dp)
                        )
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            reviews.forEach { review ->
                                ReviewCard(
                                    review = review,
                                    userRole = userRole,
                                    onDeleteConfirmed = { reviewId ->
                                        restaurantsViewModel.deleteReview(restaurantId, reviewId)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        ExtendedFloatingActionButton(
            onClick = onAddReviewClick,
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            text = { Text(stringResource(R.string.add_review)) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 24.dp)
        )
    }
}
