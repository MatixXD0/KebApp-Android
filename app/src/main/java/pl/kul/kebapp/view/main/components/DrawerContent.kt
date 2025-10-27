package pl.kul.kebapp.view.main.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import pl.kul.kebapp.R
import pl.kul.kebapp.navigation.Screen
import pl.kul.kebapp.viewmodel.AuthViewModel

@Composable
fun DrawerContent(
    authViewModel: AuthViewModel,
    onNavigate: (String) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val userRole by authViewModel.userRole.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = stringResource(R.string.avatar),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(48.dp)
                    .padding(end = 8.dp)
            )
            Text(
                text = user?.email ?: stringResource(R.string.user),
                style = MaterialTheme.typography.titleMedium
            )
        }

        DrawerMenuItem(
            titleResId = Screen.AllRestaurantScreen.title,
            iconResId = R.drawable.baseline_restaurant_24
        ) {
            onNavigate(Screen.AllRestaurantScreen.route)
        }

        DrawerMenuItem(
            titleResId = Screen.MapScreen.title,
            iconResId = R.drawable.baseline_map_24
        ) {
            onNavigate(Screen.MapScreen.route)
        }

        DrawerMenuItem(
            titleResId = Screen.FavoriteRestaurantsScreen.title,
            iconResId = R.drawable.baseline_favorite_24
        ) {
            onNavigate(Screen.FavoriteRestaurantsScreen.route)
        }

        DrawerMenuItem(
            titleResId = Screen.AddRestaurantScreen.title,
            iconResId = R.drawable.baseline_add_24
        ) {
            onNavigate(Screen.AddRestaurantScreen.route)
        }

        DrawerMenuItem(
            titleResId = Screen.SettingsScreen.title,
            iconResId = R.drawable.baseline_settings_24
        ) {
            onNavigate(Screen.SettingsScreen.route)
        }

        if (userRole == "admin") {
            DrawerMenuItem(
                titleResId = Screen.PendingRestaurantsScreen.title,
                iconResId = R.drawable.baseline_checklist_24
            ) {
                onNavigate(Screen.PendingRestaurantsScreen.route)
            }
        }

        if (userRole == "admin") {
            DrawerMenuItem(
                titleResId = Screen.AdminReviewsScreen.title,
                iconResId = R.drawable.baseline_checklist_24
            ) {
                onNavigate(Screen.AdminReviewsScreen.route)
            }
        }
    }
}

@Composable
private fun DrawerMenuItem(
    titleResId: Int,
    iconResId: Int,
    onClick: () -> Unit,
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(48.dp)
                .padding(end = 16.dp)
        )
        Text(
            text = stringResource(id = titleResId),
            style = MaterialTheme.typography.bodyLarge
        )
    }

    Spacer(Modifier.height(12.dp))
}
