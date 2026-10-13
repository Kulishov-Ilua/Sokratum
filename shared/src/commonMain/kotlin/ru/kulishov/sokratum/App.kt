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
import androidx.navigation.toRoute
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import ru.kulishov.sokratum.data.remote.KtorClient
import ru.kulishov.sokratum.domain.model.SpaceRedactTransfer
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.authScreen.AuthScreenUI
import ru.kulishov.sokratum.ui.authScreen.AuthScreenViewModel
import ru.kulishov.sokratum.ui.homeScreen.HomeScreenUI
import ru.kulishov.sokratum.ui.homeScreen.HomeScreenViewModel
import ru.kulishov.sokratum.ui.navigation.NavigationRoutes
import ru.kulishov.sokratum.ui.redactChatScreen.RedactChatScreenUI
import ru.kulishov.sokratum.ui.redactChatScreen.RedactChatScreenViewModel
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceUI
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceScreenViewModel
import ru.kulishov.sokratum.ui.settingScreen.SettingScreenUI
import ru.kulishov.sokratum.ui.settingScreen.SettingScreenViewModel
import ru.kulishov.sokratum.ui.spaceScreen.SpaceScreenUI
import ru.kulishov.sokratum.ui.spaceScreen.SpaceScreenViewModel

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
                Box(Modifier.padding(paddingValues).padding(horizontal = 16.dp)) {
                    NavHost(navController, startDestination = NavigationRoutes.AuthScreen) {
                        composable<NavigationRoutes.AuthScreen> {
                            AuthScreenUI(
                                viewModel = koinViewModel<AuthScreenViewModel>(),
                                authSuccess = { navController.navigate(NavigationRoutes.HomeScreen) }
                            )
                        }
                        composable<NavigationRoutes.HomeScreen> {
                            HomeScreenUI(
                                viewModel = koinViewModel<HomeScreenViewModel>(),
                                onNavigate = {
                                    when (it) {
                                        HomeScreenViewModel.HomeScreenNavigationRoutes.Auth -> {
                                            KtorClient.unAuth()
                                            navController.navigate(NavigationRoutes.AuthScreen)
                                        }

                                        is HomeScreenViewModel.HomeScreenNavigationRoutes.Chat -> TODO()
                                        HomeScreenViewModel.HomeScreenNavigationRoutes.NewChat -> TODO()
                                        HomeScreenViewModel.HomeScreenNavigationRoutes.Settings -> navController.navigate(
                                            NavigationRoutes.SettingScreen
                                        )

                                        is HomeScreenViewModel.HomeScreenNavigationRoutes.Space -> navController.navigate(
                                            NavigationRoutes.SpaceScreen(
                                                spaceId =   (it as HomeScreenViewModel.HomeScreenNavigationRoutes.Space).spaceId,
                                                spaceName = (it as HomeScreenViewModel.HomeScreenNavigationRoutes.Space).spaceName)
                                            )
                                        is HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor -> {
                                            navController.navigate(NavigationRoutes.SpaceRedactScreen(
                                                    spaceId =   (it as HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor).space.id,
                                                    spaceName = (it as HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor).space.name)
                                            )
                                        }
                                    }
                                }
                            )
                        }
                        composable<NavigationRoutes.SettingScreen> {
                            SettingScreenUI(
                                viewModel = koinViewModel<SettingScreenViewModel>(),
                                onNavigate = {
                                    when (it) {
                                        SettingScreenViewModel.SettingScreenNavigationRoutes.Back -> navController.navigate(
                                            NavigationRoutes.HomeScreen
                                        )

                                        SettingScreenViewModel.SettingScreenNavigationRoutes.LogOut -> {
                                            KtorClient.unAuth()
                                            navController.navigate(NavigationRoutes.AuthScreen)
                                        }
                                    }
                                }
                            )
                        }

                        composable<NavigationRoutes.SpaceRedactScreen> { backStackEntry ->
                            val space =
                                backStackEntry.toRoute<NavigationRoutes.SpaceRedactScreen>().spaceId
                            val name = backStackEntry.toRoute<NavigationRoutes.SpaceRedactScreen>().spaceName
                            val viewModel = koinViewModel<RedactSpaceScreenViewModel>()
                            viewModel.init(SpaceRedactTransfer(space,name))
                            RedactSpaceUI(viewModel, {
                                when (it) {
                                    RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Home -> navController.navigate(
                                        NavigationRoutes.HomeScreen)
                                    is RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Space -> navController.navigate(
                                        NavigationRoutes.SpaceScreen(
                                            spaceId =   (it as RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Space).spaceId,
                                            spaceName = (it as RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Space).spaceName)
                                        )
                                }
                            })
                        }
                        composable<NavigationRoutes.ChatRedactScreen> {backStackEntry ->
                            val chatId =
                                backStackEntry.toRoute<NavigationRoutes.ChatRedactScreen>().chatId
                            val spaceId = backStackEntry.toRoute<NavigationRoutes.ChatRedactScreen>().spaceId
                            RedactChatScreenUI(
                                viewModel = koinViewModel<RedactChatScreenViewModel> {
                                    parametersOf(chatId,spaceId)
                                },
                                onNavigate = {
                                    when(it){
                                        RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Back -> navController.navigate(
                                            NavigationRoutes.HomeScreen)
                                        is RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Chat -> navController.navigate(
                                            NavigationRoutes.HomeScreen)
                                    }
                                }
                            )
                        }
                        composable<NavigationRoutes.SpaceScreen> { backStackEntry ->
                            val (spaceId, spaceName) = backStackEntry.toRoute<NavigationRoutes.SpaceScreen>()
                            SpaceScreenUI(
                                viewModel = koinViewModel<SpaceScreenViewModel>{
                                    parametersOf(spaceId) },
                                onNavigate = {
                                    when(it){
                                        SpaceScreenViewModel.SpaceScreenNavigationRoutes.Back -> navController.navigate(
                                            NavigationRoutes.HomeScreen)
                                        is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatRedact -> {
                                            navController.navigate(NavigationRoutes.ChatRedactScreen(it.chatId,it.spaceId))
                                        }
                                        is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatScreen -> TODO()
                                        is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatWithMessageSend -> TODO()
                                        is SpaceScreenViewModel.SpaceScreenNavigationRoutes.SpaceRedact -> navController.navigate(
                                            NavigationRoutes.SpaceRedactScreen(spaceId,spaceName))
                                    }
                                }
                            )
                        }
                    }
                }

            }
        )
    }
}