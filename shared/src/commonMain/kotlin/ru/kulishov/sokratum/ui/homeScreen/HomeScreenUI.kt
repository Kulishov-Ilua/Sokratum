package ru.kulishov.sokratum.ui.homeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.domain.model.Space
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.ChatCardUI
import ru.kulishov.sokratum.ui.elements.SpaceCardUI
import ru.kulishov.sokratum.ui.elements.UpsElement
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.add
import sokratum.shared.generated.resources.exit
import sokratum.shared.generated.resources.keyboard_arrow_right
import sokratum.shared.generated.resources.log_in
import sokratum.shared.generated.resources.quick_chat
import sokratum.shared.generated.resources.recent_chats
import sokratum.shared.generated.resources.search
import sokratum.shared.generated.resources.settings
import sokratum.shared.generated.resources.spaces
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    viewModel: HomeScreenViewModel,
    onNavigate: (route: HomeScreenViewModel.HomeScreenNavigationRoutes) -> Unit
) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(HomeScreenViewModel.HomeScreenUiState.Loading)


    if ((DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone)) {
        when (uiState.value) {
            is HomeScreenViewModel.HomeScreenUiState.Error -> {
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
                        onClick = { onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.Auth) },
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    ) {
                        Text(
                            text = stringResource(Res.string.log_in),
                            style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                        )
                    }
                }
            }

            HomeScreenViewModel.HomeScreenUiState.Loading -> {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }
            }

            is HomeScreenViewModel.HomeScreenUiState.Success -> {
                Column() {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        val searchBarState = rememberSearchBarState()
                        val textFieldState = rememberTextFieldState()
                        val inputField = @Composable {
                            SearchBarDefaults.InputField(
                                searchBarState = searchBarState,
                                textFieldState = textFieldState,
                                onSearch = {
                                },
                                placeholder = { Text(stringResource(Res.string.search)) },
                                leadingIcon = {
                                    Icon(
                                        painter = painterResource(Res.drawable.search),
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    if (textFieldState.text.isNotEmpty()) {
                                        IconButton(onClick = { textFieldState.clearText() }) {
                                            Icon(
                                                painter = painterResource(Res.drawable.exit),
                                                contentDescription = "clear"
                                            )
                                        }
                                    }
                                }
                            )
                        }

                        Row(Modifier.height(56.dp), horizontalArrangement = Arrangement.spacedBy(15.dp)) {
                            Box(Modifier.weight(1f)) {
                                SearchBar(
                                    state = searchBarState,
                                    inputField = inputField,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (textFieldState.text.isEmpty()) {
                                Box(
                                    Modifier.size(56.dp).clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceContainer)
                                        .clickable {
                                            onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.Settings)
                                        }, contentAlignment = Alignment.Center){
                                    Icon(painterResource(Res.drawable.settings), contentDescription = "settings")
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.heightIn(min = 200.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                stringResource(Res.string.recent_chats),
                                style = MaterialTheme.typography.titleLarge
                            )
                            val nowTime = Clock.System.now()
                            for (chat in (uiState.value as HomeScreenViewModel.HomeScreenUiState.Success).chats) {
                                val findSpace =
                                    (uiState.value as HomeScreenViewModel.HomeScreenUiState.Success).spaces.find { it.id == chat.spaceId }
                                ChatCardUI(
                                    chat.name,
                                    findSpace?.name,
                                    chat.lastOpen,
                                    nowTime.toLocalDateTime(TimeZone.UTC),
                                    {onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.Chat(chat.id,chat.spaceId,chat.name))}
                                )
                            }
                        }
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Text(
                                    stringResource(Res.string.spaces),
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Icon(painterResource(Res.drawable.add), contentDescription = "Add",
                                    modifier = Modifier.clickable{
                                        onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor(
                                            Space
                                                (
                                                id = -1,
                                                name = "",
                                                lastOpen = Clock.System.now().toLocalDateTime(
                                                    TimeZone.UTC)
                                            )))
                                    })
                            }
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                items( (uiState.value as HomeScreenViewModel.HomeScreenUiState.Success).spaces){space ->
                                    SpaceCardUI(
                                        space.name,
                                        onRedact = {onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.SpaceRedactor(space))},
                                        onClick = {onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.Space(space.id, space.name))}
                                    )
                                }
                            }
                        }
                        if((uiState.value as HomeScreenViewModel.HomeScreenUiState.Success).status.isBlank()){
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
                                                onNavigate(HomeScreenViewModel.HomeScreenNavigationRoutes.NewChat(textFieldState.text.toString()))
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

                        }else{

                        }
                    }

                }

            }
        }
    } else {

    }
}