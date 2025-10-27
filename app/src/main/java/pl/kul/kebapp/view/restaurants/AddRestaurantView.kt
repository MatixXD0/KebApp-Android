package pl.kul.kebapp.view.restaurants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.kul.kebapp.R
import pl.kul.kebapp.view.restaurants.components.SectionTitle
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun AddRestaurantView(
    viewModel: RestaurantsViewModel,
    onRestaurantAdded: () -> Unit
) {
    val state by viewModel.restaurantFormState.collectAsState()

    val isFormFilled = listOf(
        state.name, state.street, state.streetNumber, state.postalCode, state.city
    ).all { it.isNotBlank() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(stringResource(R.string.add_new_restaurant), style = MaterialTheme.typography.headlineSmall)

        SectionTitle(stringResource(R.string.restaurant_details))

        ValidatedTextField(
            value = state.name,
            onValueChange = viewModel::updateName,
            label = stringResource(R.string.name),
            error = state.errors.name
        )

        SectionTitle(stringResource(R.string.address))

        ValidatedTextField(
            value = state.street,
            onValueChange = viewModel::updateStreet,
            label = stringResource(R.string.street),
            error = state.errors.street
        )

        ValidatedTextField(
            value = state.streetNumber,
            onValueChange = viewModel::updateStreetNumber,
            label = stringResource(R.string.street_number),
            error = state.errors.streetNumber
        )

        ValidatedTextField(
            value = state.postalCode,
            onValueChange = viewModel::updatePostalCode,
            label = stringResource(R.string.postal_code_xx_xxx),
            error = state.errors.postalCode
        )

        ValidatedTextField(
            value = state.city,
            onValueChange = viewModel::updateCity,
            label = stringResource(R.string.city),
            error = state.errors.city
        )

        state.errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Button(
            onClick = { viewModel.submitRestaurant(onSuccess = onRestaurantAdded) },
            enabled = !state.isSubmitting && isFormFilled,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isSubmitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            } else {
                Text(stringResource(R.string.add_restaurant), style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
fun ValidatedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null,
        supportingText = { error?.let { Text(it) } },
        modifier = modifier.fillMaxWidth()
    )
}
