package ru.kulishov.sokratum

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.viewmodel.koinViewModel
import org.koin.plugin.module.dsl.koinApplication
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.authScreen.AuthScreenUI
import ru.kulishov.sokratum.ui.authScreen.AuthScreenViewModel
import ru.kulishov.sokratum.ui.navigation.NavigationRoutes

@Composable
@Preview
fun App(
    onNavHostReady: suspend (NavController) -> Unit = {}
) {
    val windowWidth = LocalWindowInfo.current.containerDpSize.width

    val platform = if (windowWidth < 600.dp) DeviceData.Platforms.Phone(
        DeviceData.OS.NotFound,
        ""
    ) else DeviceData.Platforms.Desktop(DeviceData.OS.NotFound, "${windowWidth.value.toInt()}")
    val width = 1195

    val navController = rememberNavController()

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }

    MaterialTheme {
        Scaffold(
            content = { paddingValues ->
                Box(Modifier.padding(paddingValues)){
                    NavHost(navController, startDestination = NavigationRoutes.AuthScreen){
                        composable<NavigationRoutes.AuthScreen> {
                            AuthScreenUI(
                                viewModel = koinViewModel<AuthScreenViewModel>(),
                                authSuccess = {navController.navigate(NavigationRoutes.HomeScreen)}
                            )
                        }
                        composable<NavigationRoutes.HomeScreen> {

                        }
                    }
                }

            }
        )
    }
}