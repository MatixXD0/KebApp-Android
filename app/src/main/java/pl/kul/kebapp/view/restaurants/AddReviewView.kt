package pl.kul.kebapp.view.restaurants

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import pl.kul.kebapp.R
import pl.kul.kebapp.view.restaurants.components.RatingSlider
import pl.kul.kebapp.view.restaurants.components.SectionTitle
import pl.kul.kebapp.viewmodel.RestaurantsViewModel

@Composable
fun AddReviewView(
    restaurantId: String,
    viewModel: RestaurantsViewModel,
    onReviewAdded: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val state by viewModel.reviewFormState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(stringResource(R.string.add_your_review), style = MaterialTheme.typography.headlineSmall)

        SectionTitle(title = stringResource(R.string.rate_different_aspects))

        RatingSlider(stringResource(R.string.taste), state.taste, viewModel::updateTaste)
        RatingSlider(stringResource(R.string.quality), state.quality, viewModel::updateQuality)
        RatingSlider(stringResource(R.string.venue), state.venue, viewModel::updateVenue)
        RatingSlider(stringResource(R.string.value_for_money), state.valueForMoney, viewModel::updateValueForMoney)

        SectionTitle(title = stringResource(R.string.additional_comment))

        OutlinedTextField(
            value = state.comment,
            onValueChange = viewModel::updateComment,
            label = { Text(stringResource(R.string.comment)) },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp),
            maxLines = 5
        )

        SectionTitle(title = stringResource(R.string.overall_rating))
        RatingSlider(stringResource(R.string.overall), state.overall, viewModel::updateOverall)

        SectionTitle(title = stringResource(R.string.summary))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.would_you_repeat), style = MaterialTheme.typography.bodyLarge)
            Switch(
                checked = state.wouldRepeat,
                onCheckedChange = viewModel::updateWouldRepeat
            )
        }

        Button(
            onClick = {
                if (!viewModel.isReviewValid(userId)) {
                    Log.e("Review", "Invalid form state")
                    return@Button
                }

                viewModel.submitReview(
                    restaurantId = restaurantId,
                    userId = userId!!,
                    onSuccess = onReviewAdded,
                    onFailure = { Log.e("Review", "Failed to submit review") }
                )
            },
            enabled = !state.isSubmitting && state.comment.length in 5..500,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            if (state.isSubmitting)
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            else
                Text(stringResource(R.string.submit_review), style = MaterialTheme.typography.titleMedium)
        }
    }
}
