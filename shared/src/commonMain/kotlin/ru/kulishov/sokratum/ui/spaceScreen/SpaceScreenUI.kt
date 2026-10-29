package ru.kulishov.sokratum.ui.spaceScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.ChatCardUI
import ru.kulishov.sokratum.ui.elements.MessageBar
import ru.kulishov.sokratum.ui.elements.UpsElement
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.add
import sokratum.shared.generated.resources.back
import sokratum.shared.generated.resources.chats
import sokratum.shared.generated.resources.discuss_something
import sokratum.shared.generated.resources.keyboard_arrow_right
import sokratum.shared.generated.resources.new_chat
import sokratum.shared.generated.resources.new_message
import sokratum.shared.generated.resources.no_internet_connection
import sokratum.shared.generated.resources.quick_chat
import sokratum.shared.generated.resources.settings
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpaceScreenUI(
    platform: DeviceData.Platforms,
    viewModel: SpaceScreenViewModel,
    onNavigate: (SpaceScreenViewModel.SpaceScreenNavigationRoutes) -> Unit
) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(SpaceScreenViewModel.SpaceScreenUiState.Loading)
    when (uiState.value) {
        is SpaceScreenViewModel.SpaceScreenUiState.Error -> {
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
                    onClick = { onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.Back) },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.back),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }

        SpaceScreenViewModel.SpaceScreenUiState.Loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        is SpaceScreenViewModel.SpaceScreenUiState.Success -> {
            val chats = (uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).chats
            val name =
                (uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).spaceName
            val id = (uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).spaceId
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = if ((platform == null || platform is DeviceData.Platforms.Phone || platform is DeviceData.Platforms.WebPhone)) Arrangement.Center else Arrangement.Start
            ) {
                Column(modifier = Modifier.padding(vertical = 15.dp).widthIn(max = 400.dp)) {


                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(15.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.Back)
                                }
                            ) {
                                Icon(
                                    painterResource(Res.drawable.back),
                                    contentDescription = "back"
                                )
                            }
                            Text(
                                name,
                                style = MaterialTheme.typography.titleLarge,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    onNavigate(
                                        SpaceScreenViewModel.SpaceScreenNavigationRoutes.SpaceRedact(
                                            id,
                                            name
                                        )
                                    )
                                }
                            ) {
                                Icon(
                                    painterResource(Res.drawable.settings),
                                    contentDescription = "settigs"
                                )
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.chats),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                IconButton(
                                    onClick = {
                                        onNavigate(
                                            SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatRedact(
                                                -1,
                                                "",
                                                spaceId = id
                                            )
                                        )
                                    }
                                ) {
                                    Icon(
                                        painterResource(Res.drawable.add),
                                        contentDescription = "Add"
                                    )
                                }

                            }
                            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                items(chats) { chat ->
                                    ChatCardUI(
                                        name = chat.name,
                                        lastOpen = chat.lastOpen,
                                        now = now,
                                        onClick = {
                                            onNavigate(
                                                SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatScreen(
                                                    chat.id,
                                                    chat.spaceId,
                                                    chat.name
                                                )
                                            )
                                        }
                                    )
                                }
                            }
                            if ((platform == null || platform is DeviceData.Platforms.Phone || platform is DeviceData.Platforms.WebPhone)) {
                                if ((uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).status.isBlank()) {
                                    var query by remember { mutableStateOf("") }
                                    MessageBar(
                                        query = query,
                                        onQueryChange = { query=it },
                                        onSend = {
                                            onNavigate(
                                                SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatWithMessageSend(
                                                    query,
                                                    id
                                                )
                                            )
                                        },
                                        placeholder = stringResource(Res.string.new_chat),
                                        leadingIcon = null,
                                        trailingIcon = {
                                            Icon(
                                                painter = painterResource(Res.drawable.keyboard_arrow_right),
                                                contentDescription = "send"
                                            )
                                        },
                                        modifier = Modifier.height(56.dp)
                                    )

                                } else {
                                    Box(
                                        Modifier.padding(bottom = 15.dp).fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            Modifier.clip(CircleShape)
                                                .background(
                                                    MaterialTheme.colorScheme.errorContainer
                                                )
                                        ) {
                                            Text(
                                                stringResource(Res.string.no_internet_connection),
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    color = MaterialTheme.colorScheme.onErrorContainer
                                                ),
                                                modifier = Modifier.padding(
                                                    horizontal = 16.dp,
                                                    vertical = 8.dp
                                                )
                                            )
                                        }
                                    }
                                }
                            }


                        }


                    }
                }
                if (!(platform == null || platform is DeviceData.Platforms.Phone || platform is DeviceData.Platforms.WebPhone)) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.padding(vertical = 25.dp, horizontal = 25.dp)
                                .widthIn(max = 825.dp)
                        ) {
                            Text(
                                stringResource(Res.string.discuss_something),
                                style = MaterialTheme.typography.headlineSmall,
                                textAlign = TextAlign.Center
                            )
                            if ((uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).status.isBlank()) {
                                var query by remember { mutableStateOf("") }
                                MessageBar(
                                    query = query,
                                    onQueryChange = { query=it },
                                    onSend = {
                                        onNavigate(
                                            SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatWithMessageSend(
                                                query,
                                                id
                                            )
                                        )
                                    },
                                    placeholder = stringResource(Res.string.new_chat),
                                    leadingIcon = null,
                                    trailingIcon = {
                                        Icon(
                                            painter = painterResource(Res.drawable.keyboard_arrow_right),
                                            contentDescription = "send"
                                        )
                                    },
                                    modifier = Modifier.height(56.dp)
                                )
                            } else {
                                Box(
                                    Modifier.padding(bottom = 15.dp).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Box(
                                        Modifier.clip(CircleShape)
                                            .background(
                                                MaterialTheme.colorScheme.errorContainer
                                            )
                                    ) {
                                        Text(
                                            stringResource(Res.string.no_internet_connection),
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                color = MaterialTheme.colorScheme.onErrorContainer
                                            ),
                                            modifier = Modifier.padding(
                                                horizontal = 16.dp,
                                                vertical = 8.dp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}