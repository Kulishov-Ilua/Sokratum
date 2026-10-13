package ru.kulishov.sokratum.ui.spaceScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.ChatCardUI
import ru.kulishov.sokratum.ui.elements.UpsElement
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.add
import sokratum.shared.generated.resources.back
import sokratum.shared.generated.resources.chats
import sokratum.shared.generated.resources.keyboard_arrow_right
import sokratum.shared.generated.resources.quick_chat
import sokratum.shared.generated.resources.settings
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SpaceScreenUI(
    viewModel: SpaceScreenViewModel,
    onNavigate:(SpaceScreenViewModel.SpaceScreenNavigationRoutes)->Unit
) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(SpaceScreenViewModel.SpaceScreenUiState.Loading)
    if ((DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone)) {
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
                val name = (uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).spaceName
                val id =  (uiState.value as SpaceScreenViewModel.SpaceScreenUiState.Success).spaceId
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
                            Icon(painterResource(Res.drawable.back), contentDescription = "back")
                        }
                        Text(
                            name,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.SpaceRedact(id, name))
                            }
                        ){
                          Icon(painterResource(Res.drawable.settings), contentDescription = "settigs")
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
                                    onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatRedact(-1,"", spaceId = id))
                                }
                            ){
                                Icon(painterResource(Res.drawable.add), contentDescription = "Add")
                            }

                        }
                        val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.weight(1f)) {
                            items(chats){ chat->
                                ChatCardUI(
                                    name = chat.name,
                                    lastOpen = chat.lastOpen,
                                    now = now,
                                    onClick = {
                                        onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatScreen(chat.id))
                                    }
                                )
                            }
                        }
                    }

                    val searchBarState = rememberSearchBarState()
                    val textFieldState = rememberTextFieldState()
                    val inputField = @Composable {
                        SearchBarDefaults.InputField(
                            searchBarState = searchBarState,
                            textFieldState = textFieldState,
                            onSearch = {
                                //todo chat
                            },
                            placeholder = { Text(stringResource(Res.string.quick_chat)) },
                            leadingIcon = null,
                            trailingIcon = {
                                if (textFieldState.text.isNotEmpty()) {
                                    IconButton(onClick = {
                                        onNavigate(SpaceScreenViewModel.SpaceScreenNavigationRoutes.ChatWithMessageSend(textFieldState.text.toString(),id))
                                    }) {
                                        Icon(
                                            painter = painterResource(Res.drawable.keyboard_arrow_right),
                                            contentDescription = "send"
                                        )
                                    }
                                }
                            }
                        )
                    }
                    SearchBar(
                        state = searchBarState,
                        inputField = inputField,
                        modifier = Modifier.padding(bottom = 15.dp).fillMaxWidth()
                    )
                }
            }
        }
    }else{

    }
}