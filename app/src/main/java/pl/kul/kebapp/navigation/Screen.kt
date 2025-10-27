package pl.kul.kebapp.navigation

import androidx.annotation.StringRes
import pl.kul.kebapp.R


sealed class Screen(
    val route: String,
    @StringRes
    val title: Int
) {
    data object LoginScreen : Screen(
        "login_screen",
        R.string.screen_title_login
    )

    data object RegisterScreen : Screen(
        "register_screen",
        R.string.screen_title_register
    )

    data object SettingsScreen : Screen(
        "settings_screen",
        R.string.screen_title_settings
    )

    data object ThemeSwitcherScreen : Screen(
        "theme_switcher_screen",
        R.string.screen_title_theme_switcher
    )

    data object AddRestaurantScreen : Screen(
        "add_restaurant_screen",
        R.string.screen_title_add_restaurant
    )

    data object AllRestaurantScreen : Screen(
        "all_restaurant_screen",
        R.string.screen_title_all_restaurant
    )

    object RestaurantDetailsScreen : Screen(
        "restaurant_details_screen/{restaurantId}",
        R.string.screen_title_restaurant_details
    ) {
        fun createRoute(restaurantId: String) = "restaurant_details_screen/$restaurantId"
    }

    data object AddReviewScreen : Screen(
        "add_review_screen/{restaurantId}",
        R.string.screen_title_add_review
    ) {
        fun createRoute(restaurantId: String) = "add_review_screen/$restaurantId"
    }

    data object MapScreen : Screen(
        "map_screen",
        R.string.map_screen
    )

    data object FavoriteRestaurantsScreen : Screen(
        "favorite_restaurants_screen",
        R.string.favorite_restaurants
    )

    data object PendingRestaurantsScreen : Screen(
        "pending_restaurants_screen",
        R.string.manage_restaurants
    )

    data object AdminReviewsScreen : Screen(
        "admin_reviews_screen",
        R.string.admin_all_reviews
    )

    companion object {
        private val allScreens = listOf(
            LoginScreen,
            RegisterScreen,
            SettingsScreen,
            ThemeSwitcherScreen,
            AddRestaurantScreen,
            AllRestaurantScreen,
            RestaurantDetailsScreen,
            AddReviewScreen,
            MapScreen,
            FavoriteRestaurantsScreen,
            PendingRestaurantsScreen,
            AdminReviewsScreen
        )

        fun fromRoute(route: String?): Screen? {
            if (route == null) return null
            return allScreens.find { screen ->
                val baseRoute = screen.route.substringBefore("/{")
                route.startsWith(baseRoute)
            }
        }
    }
}
