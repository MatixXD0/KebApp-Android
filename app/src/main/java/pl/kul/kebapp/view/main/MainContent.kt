package pl.kul.kebapp.view.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import pl.kul.kebapp.navigation.MainNavigation
import pl.kul.kebapp.navigation.Screen
import pl.kul.kebapp.view.main.components.BottomBar
import pl.kul.kebapp.view.main.components.BottomSheetContent
import pl.kul.kebapp.view.main.components.DrawerContent
import pl.kul.kebapp.view.main.components.TopBar
import pl.kul.kebapp.viewmodel.AuthViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent(
    authViewModel: AuthViewModel,
    onLocaleChange: (Locale) -> Unit
) {

    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentScreen = Screen.fromRoute(currentRoute)
    val gesturesEnabled = currentRoute != Screen.MapScreen.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            Surface(
                modifier = Modifier.width(280.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                DrawerContent(
                    authViewModel = authViewModel,
                    onNavigate = { route ->
                        scope.launch { drawerState.close() }
                        navController.navigate(route)
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopBar(
                    screen = currentScreen,
                    onHamburgerClick = { scope.launch { drawerState.open() } },
                    onMoreClick = { showBottomSheet = true }
                )
            },
            bottomBar = {
                BottomBar(currentScreen, navController)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                MainNavigation(
                    navController = navController,
                    authViewModel = authViewModel,
                    onLocaleChange = onLocaleChange,
                    onCloseBottomSheet = { showBottomSheet = false }
                )
                if (showBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = { showBottomSheet = false },
                        sheetState = sheetState
                    ) {
                        BottomSheetContent(
                            onSettingsClick = {
                                showBottomSheet = false
                                navController.navigate(Screen.SettingsScreen.route)
                            },
                            onLogoutClick = {
                                showBottomSheet = false
                                authViewModel.logout()
                            }
                        )
                    }
                }
            }
        }
    }
}
