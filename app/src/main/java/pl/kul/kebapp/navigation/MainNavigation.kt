package pl.kul.kebapp.navigation

import AdminReviewsView
import FavoriteRestaurantsView
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pl.kul.kebapp.navigation.Screen.RestaurantDetailsScreen
import pl.kul.kebapp.view.map.MapView
import pl.kul.kebapp.view.restaurants.AddRestaurantView
import pl.kul.kebapp.view.restaurants.AddReviewView
import pl.kul.kebapp.view.restaurants.PendingRestaurantsView
import pl.kul.kebapp.view.restaurants.RestaurantDetailsView
import pl.kul.kebapp.view.restaurants.RestaurantListView
import pl.kul.kebapp.view.settings.SettingsView
import pl.kul.kebapp.viewmodel.AuthViewModel
import pl.kul.kebapp.viewmodel.LocationViewModel
import pl.kul.kebapp.viewmodel.RestaurantsViewModel
import java.util.Locale

@Composable
fun MainNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    onLocaleChange: (Locale) -> Unit,
    onCloseBottomSheet: () -> Unit
) {
    val restaurantsViewModel: RestaurantsViewModel = viewModel()
    val locationViewModel: LocationViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.AllRestaurantScreen.route
    ) {

        composable(Screen.SettingsScreen.route) {
            SettingsView(
                onLogout = {
                    onCloseBottomSheet()
                    authViewModel.logout()
                },
                onLocaleChange = onLocaleChange
            )
        }

        composable(Screen.AddRestaurantScreen.route) {
            AddRestaurantView(
                viewModel = restaurantsViewModel,
                onRestaurantAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AllRestaurantScreen.route) {
            RestaurantListView(
                restaurantsViewModel = restaurantsViewModel,
                locationViewModel = locationViewModel,
                onRestaurantClick = { restaurantId ->
                    navController.navigate(RestaurantDetailsScreen.createRoute(restaurantId))
                }
            )
        }

        composable(
            route = RestaurantDetailsScreen.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            RestaurantDetailsView(
                restaurantId = restaurantId,
                restaurantsViewModel = restaurantsViewModel,
                authViewModel = authViewModel,
                onAddReviewClick = {
                    navController.navigate(Screen.AddReviewScreen.createRoute(restaurantId))
                }
            )
        }

        composable(
            route = Screen.AddReviewScreen.route,
            arguments = listOf(navArgument("restaurantId") { type = NavType.StringType })
        ) { backStackEntry ->
            val restaurantId = backStackEntry.arguments?.getString("restaurantId") ?: ""
            AddReviewView(
                restaurantId = restaurantId,
                viewModel = restaurantsViewModel,
                onReviewAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.MapScreen.route) {
            MapView(
                viewModel = restaurantsViewModel,
                locationViewModel = locationViewModel,
                navController = navController
            )
        }

        composable(Screen.FavoriteRestaurantsScreen.route) {
            FavoriteRestaurantsView(
                restaurantsViewModel = restaurantsViewModel,
                onRestaurantClick = { restaurantId ->
                    navController.navigate(RestaurantDetailsScreen.createRoute(restaurantId))
                }
            )
        }

        composable(Screen.PendingRestaurantsScreen.route) {
            PendingRestaurantsView(
                viewModel = restaurantsViewModel,
            )
        }

        composable(Screen.AdminReviewsScreen.route) {
            AdminReviewsView(
                viewModel = restaurantsViewModel,
            )
        }
    }
}
