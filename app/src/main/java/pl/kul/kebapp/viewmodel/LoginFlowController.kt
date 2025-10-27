package pl.kul.kebapp.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.kul.kebapp.navigation.AuthNavigation
import java.util.Locale

@Composable
fun LoginFlowController(onLocaleChange: (Locale) -> Unit) {
    val authViewModel: AuthViewModel = viewModel()
    val isUserLoggedIn by authViewModel.authState.collectAsState()

    AuthNavigation(
        isUserLoggedIn = isUserLoggedIn,
        onLocaleChange = onLocaleChange,
        authViewModel = authViewModel
    )
}
