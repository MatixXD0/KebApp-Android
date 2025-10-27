package pl.kul.kebapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import pl.kul.kebapp.view.auth.LoginView
import pl.kul.kebapp.view.auth.RegisterView
import pl.kul.kebapp.view.main.MainContent
import pl.kul.kebapp.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun AuthNavigation(
    isUserLoggedIn: Boolean,
    onLocaleChange: (Locale) -> Unit,
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    if (isUserLoggedIn) {
        MainContent(
            authViewModel = authViewModel,
            onLocaleChange = onLocaleChange
        )
    } else {
        NavHost(navController = navController, startDestination = Screen.LoginScreen.route) {
            composable(Screen.LoginScreen.route) {
                LoginView(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Screen.RegisterScreen.route)
                    }
                )
            }

            composable(Screen.RegisterScreen.route) {
                RegisterView(
                    viewModel = authViewModel,
                    onNavigateToLogin = {
                        navController.navigate(Screen.LoginScreen.route)
                    }
                )
            }
        }
    }
}
