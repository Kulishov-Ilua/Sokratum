package ru.kulishov.sokratum.ui.settingScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.data.remote.KtorClient
import ru.kulishov.sokratum.getPlatform
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.DeviceCardUI
import ru.kulishov.sokratum.ui.elements.UpsElement
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.back
import sokratum.shared.generated.resources.computer
import sokratum.shared.generated.resources.connected_devices
import sokratum.shared.generated.resources.key
import sokratum.shared.generated.resources.log_in
import sokratum.shared.generated.resources.logout
import sokratum.shared.generated.resources.settings
import sokratum.shared.generated.resources.smartphone
import sokratum.shared.generated.resources.tablet_mac
import sokratum.shared.generated.resources.visibility
import sokratum.shared.generated.resources.visibility_off

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingScreenUI(
    viewModel: SettingScreenViewModel,
    onNavigate: (SettingScreenViewModel.SettingScreenNavigationRoutes) -> Unit
) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(SettingScreenViewModel.SettingScreenUiState.Loading)
    when (uiState.value) {
        is SettingScreenViewModel.SettingScreenUiState.Error -> {
            Column(
                modifier = Modifier.widthIn(max = 825.dp),
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Box(
                        Modifier.size(56.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable {
                                onNavigate(SettingScreenViewModel.SettingScreenNavigationRoutes.Back)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.back),
                            contentDescription = "back"
                        )
                    }
                    Text(
                        stringResource(Res.string.settings),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center
                    )
                    Box(
                        Modifier.size(56.dp).clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceContainer)
                            .clickable {
                                onNavigate(SettingScreenViewModel.SettingScreenNavigationRoutes.LogOut)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.logout),
                            contentDescription = "Log out",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    UpsElement()
                }
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Button(
                        onClick = { onNavigate(SettingScreenViewModel.SettingScreenNavigationRoutes.Back) },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.back),
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }
        }

        SettingScreenViewModel.SettingScreenUiState.Loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        is SettingScreenViewModel.SettingScreenUiState.Success -> {
            val devices =
                (uiState.value as SettingScreenViewModel.SettingScreenUiState.Success).items
            Column(Modifier.padding(top=15.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier = Modifier.widthIn(max = 825.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Box(
                            Modifier.size(56.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    onNavigate(SettingScreenViewModel.SettingScreenNavigationRoutes.Back)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.back),
                                contentDescription = "back"
                            )
                        }
                        Text(
                            stringResource(Res.string.settings),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )
                        Box(
                            Modifier.size(56.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    onNavigate(SettingScreenViewModel.SettingScreenNavigationRoutes.LogOut)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.logout),
                                contentDescription = "Log out",
                                tint = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    val deviceFind = devices.find { it.login == KtorClient.username }
                    if (deviceFind != null) {
                        Card(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp)).background(
                                MaterialTheme.colorScheme.surfaceContainer
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp),
                                modifier = Modifier.padding(
                                    horizontal = 25.dp,
                                    vertical = 20.dp
                                )
                            ) {
                                Icon(
                                    painter = painterResource(Res.drawable.computer),
                                    contentDescription = "device",
                                    modifier = Modifier.size(50.dp)
                                )
                                Column {
                                    Text(
                                        deviceFind.login,
                                        style = MaterialTheme.typography.titleLargeEmphasized
                                    )
                                    Text(
                                        deviceFind.device,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                }

                            }
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            stringResource(Res.string.connected_devices),
                            style = MaterialTheme.typography.titleLarge
                        )
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            items(devices) { device ->
                                DeviceCardUI(
                                    DeviceData.Platforms.Desktop(
                                        DeviceData.OS.NotFound,
                                        ""
                                    ), device.login, device.device
                                )
                            }
                            item {
                                TextField(
                                    value = viewModel.key,
                                    onValueChange = {},
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(
                                            painterResource(Res.drawable.key),
                                            contentDescription = "generate key"
                                        )
                                    },
                                    trailingIcon = {
                                        Icon(
                                            modifier = Modifier.clickable {
                                                if (viewModel.key.isBlank()) {
                                                    viewModel.getKey()
                                                } else {
                                                    viewModel.deleteKey()
                                                }
                                            },
                                            painter = if (viewModel.key.isBlank()) painterResource(
                                                Res.drawable.visibility
                                            ) else painterResource(Res.drawable.visibility_off),
                                            contentDescription = "get key"
                                        )
                                    },
                                    label = {
                                        Text(
                                            stringResource(Res.string.key),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    colors = TextFieldDefaults.colors().copy(
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent,
                                        errorIndicatorColor = Color.Transparent
                                    ),

                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}