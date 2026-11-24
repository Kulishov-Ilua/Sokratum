package ru.kulishov.sokratum

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import ru.kulishov.sokratum.ui.chatScreen.ChatScreenUI
import ru.kulishov.sokratum.ui.chatScreen.ChatScreenViewModel
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

    val platform = remember(windowWidth) {
        if (windowWidth < 1000.dp) {
            DeviceData.Platforms.Phone(DeviceData.OS.NotFound, "")
        } else {
            DeviceData.Platforms.Desktop(
                DeviceData.OS.NotFound,
                windowWidth.value.toInt().toString()
            )
        }
    }


    val navController = rememberNavController()

    LaunchedEffect(navController) {
        onNavHostReady(navController)
    }


    fun navigateToAuth() {
        KtorClient.unAuth()
        navController.navigate(NavigationRoutes.AuthScreen) {
            popUpTo(0) { inclusive = true }
        }
    }

    SokratumTheme() {
        Scaffold(

            content = { paddingValues ->
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
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
                                    platform = platform,
                                    viewModel = koinViewModel<HomeScreenViewModel>(),
                                    onNavigate = {
                                        when (it) {
                                            HomeScreenViewModel.HomeScreenNavigationRoutes.Auth -> {
                                                navigateToAuth()
                                            }

                                            is HomeScreenViewModel.HomeScreenNavigationRoutes.Chat -> {
                                                navController.navigate(
                                                    NavigationRoutes.ChatScreen(
                                                        chatId = it.chatID,
                                                        spaceId = it.spaceId,
                                                        chatName = it.chatName,
                                                    )
                                                )
                                            }

                                            is HomeScreenViewModel.HomeScreenNavigationRoutes.NewChat -> {
                                                navController.navigate(
                                                    NavigationRoutes.ChatScreen(
                                                        chatId = -1,
                                                        spaceId = 0,
                                                        chatName = "New chat",
                                                        message = it.message
                                                    )
                                                )
                                            }

                                            HomeScreenViewModel.HomeScreenNavigationRoutes.Settings -> navController.navigate(
                                                NavigationRoutes.SettingScreen
                                            )

                                            is HomeScreenViewModel.HomeScreenNavigationRoutes.Space -> navController.navigate(
                                                NavigationRoutes.SpaceScreen(
                                                    spaceId = it.spaceId,
                                                    spaceName = it.spaceName
                                                )
                                            )

                                            is HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor -> {
                                                navController.navigate(
                                                    NavigationRoutes.SpaceRedactScreen(
                                                        spaceId = it.space.id,
                                                        spaceName = it.space.name
                                                    )
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
                                            SettingScreenViewModel.SettingScreenNavigationRoutes.Back -> navController.popBackStack()

                                            SettingScreenViewModel.SettingScreenNavigationRoutes.LogOut -> {
                                                navigateToAuth()
                                            }
                                        }
                                    }
                                )
                            }

                            composable<NavigationRoutes.SpaceRedactScreen> { backStackEntry ->
                                val space =
                                    backStackEntry.toRoute<NavigationRoutes.SpaceRedactScreen>().spaceId
                                val name =
                                    backStackEntry.toRoute<NavigationRoutes.SpaceRedactScreen>().spaceName
                                val viewModel = koinViewModel<RedactSpaceScreenViewModel>()
                                viewModel.init(SpaceRedactTransfer(space, name))
                                RedactSpaceUI(viewModel, {
                                    when (it) {
                                        RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Home -> navController.popBackStack()

                                        is RedactSpaceScreenViewModel.RedactSpaceNavigationRoutes.Space -> navController.navigate(
                                            NavigationRoutes.SpaceScreen(
                                                spaceId = it.spaceId,
                                                spaceName = it.spaceName
                                            )
                                        )
                                    }
                                })
                            }
                            composable<NavigationRoutes.ChatRedactScreen> { backStackEntry ->
                                val chatId =
                                    backStackEntry.toRoute<NavigationRoutes.ChatRedactScreen>().chatId
                                val spaceId =
                                    backStackEntry.toRoute<NavigationRoutes.ChatRedactScreen>().spaceId
                                RedactChatScreenUI(
                                    viewModel = koinViewModel<RedactChatScreenViewModel> {
                                        parametersOf(chatId, spaceId)
                                    },
                                    onNavigate = {
                                        when (it) {
                                            RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Back -> navController.popBackStack()

                                            is RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Chat -> navController.navigate(
                                                NavigationRoutes.ChatScreen(
                                                    chatId = it.chatId,
                                                    spaceId = it.spaceId,
                                                    chatName = it.chatName,
                                                    message = ""
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                            composable<NavigationRoutes.SpaceScreen> { backStackEntry ->
                                val (spaceId, spaceName) = backStackEntry.toRoute<NavigationRoutes.SpaceScreen>()
                                SpaceScreenUI(
                                    platform = platform,
                                    viewModel = koinViewModel<SpaceScreenViewModel> {
                                        parametersOf(spaceId)
                                    },
                                    onNavigate = {
                                        when (it) {
                                            SpaceScreenViewModel.SpaceScreenNavigationRoutes.Back -> navController.popBackStack()

                                            is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatRedact -> {
                                                navController.navigate(
                                                    NavigationRoutes.ChatRedactScreen(
                                                        it.chatId,
                                                        it.spaceId
                                                    )
                                                )
                                            }

                                            is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatScreen -> navController.navigate(
                                                NavigationRoutes.ChatScreen(
                                                    chatId = it.chatID,
                                                    spaceId = it.spaceId,
                                                    chatName = it.chatName,
                                                )
                                            )

                                            is SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatWithMessageSend -> navController.navigate(
                                                NavigationRoutes.ChatScreen(
                                                    chatId = -1,
                                                    spaceId = it.spaceId,
                                                    chatName = "New chat",
                                                    message = it.message
                                                )
                                            )

                                            is SpaceScreenViewModel.SpaceScreenNavigationRoutes.SpaceRedact -> navController.navigate(
                                                NavigationRoutes.SpaceRedactScreen(
                                                    spaceId,
                                                    spaceName
                                                )
                                            )
                                        }
                                    }
                                )
                            }
                            composable<NavigationRoutes.ChatScreen> { backStackEntry ->
                                val (chatId, spaceId, chatName, message) = backStackEntry.toRoute<NavigationRoutes.ChatScreen>()
                                ChatScreenUI(koinViewModel<ChatScreenViewModel> {
                                    parametersOf(
                                        chatId,
                                        chatName,
                                        spaceId,
                                        message
                                    )
                                }, {
                                    when (it) {
                                        ChatScreenViewModel.ChatScreenNavigationRoutes.Back -> {
                                            navController.popBackStack()
                                        }

                                        is ChatScreenViewModel.ChatScreenNavigationRoutes.ChatSettings -> navController.navigate(
                                            NavigationRoutes.ChatRedactScreen(chatId, spaceId)
                                        )
                                    }
                                })
                            }
                        }
                    }
                }

            }
        )
    }
}
