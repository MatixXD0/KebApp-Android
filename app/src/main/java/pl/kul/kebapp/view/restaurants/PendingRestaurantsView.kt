package pl.kul.kebapp.view.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.model.Restaurant
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun PendingRestaurantsView(
    viewModel: RestaurantsViewModel,
) {
    val restaurants = remember { mutableStateOf<List<Restaurant>>(emptyList()) }

    val refreshPendingRestaurants = {
        viewModel.getPendingRestaurants { pendingList ->
            restaurants.value = pendingList
        }
    }

    LaunchedEffect(Unit) {
        refreshPendingRestaurants()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.restaurants_awaiting_approval),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(bottom = 16.dp)

        )

        if (restaurants.value.isEmpty()) {
            Text(
                text = stringResource(R.string.no_restaurants_to_approve),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                restaurants.value.forEach { restaurant ->
                    RestaurantApprovalCard(
                        restaurant = restaurant,
                        onApprove = {
                            viewModel.updateRestaurantStatus(restaurant.id, "approved")
                            refreshPendingRestaurants()
                        },
                        onReject = {
                            viewModel.updateRestaurantStatus(restaurant.id, "rejected")
                            refreshPendingRestaurants()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RestaurantApprovalCard(
    restaurant: Restaurant,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = restaurant.name,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoRow(
                stringResource(R.string.adres),
                "${restaurant.street} ${restaurant.streetNumber}, ${restaurant.postalCode} ${restaurant.city}"
            )
            InfoRow(
                stringResource(R.string.location),
                "${restaurant.latitude}, ${restaurant.longitude}"
            )
            InfoRow(stringResource(R.string.country), restaurant.country)

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onApprove,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.approve))
                }

                OutlinedButton(
                    onClick = onReject,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.reject))
                }
            }
        }
    }
}


@Composable
fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
