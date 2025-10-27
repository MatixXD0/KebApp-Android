package pl.kul.kebapp.view.main.components

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import pl.kul.kebapp.R
import pl.kul.kebapp.navigation.Screen

@Composable
fun BottomBar(currentScreen: Screen?, navController: NavController) {
    val items = listOf(
        Screen.AllRestaurantScreen,
        Screen.MapScreen,
        Screen.FavoriteRestaurantsScreen
    )

    NavigationBar {
        items.forEach { screen ->
            val icon = when (screen) {
                Screen.AllRestaurantScreen -> R.drawable.baseline_restaurant_24
                Screen.MapScreen -> R.drawable.baseline_map_24
                Screen.FavoriteRestaurantsScreen -> R.drawable.baseline_favorite_24
                else -> R.drawable.baseline_hub_24
            }

            NavigationBarItem(
                selected = currentScreen?.route == screen.route,
                onClick = {
                    if (currentScreen?.route != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(Screen.AllRestaurantScreen.route)
                            launchSingleTop = true
                        }
                    }
                },
                icon = { Icon(painter = painterResource(id = icon), contentDescription = screen.route) },
                label = {
                    Text(
                        text = stringResource(screen.title),
                        textAlign = TextAlign.Center,
                    )
                }
            )
        }
    }
}