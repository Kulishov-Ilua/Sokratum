package ru.kulishov.sokratum.ui.chatScreen

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.markweave.repository.MDParse
import ru.kulishov.sokratum.data.ai.AIModels
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.MessageBar
import ru.kulishov.sokratum.ui.elements.MessageUI
import ru.kulishov.sokratum.ui.elements.TextFieldCustom
import ru.kulishov.sokratum.ui.elements.UpsElement
import ru.kulishov.sokratum.ui.homeScreen.HomeScreenViewModel
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.are_you_sure
import sokratum.shared.generated.resources.back
import sokratum.shared.generated.resources.cancel
import sokratum.shared.generated.resources.delete
import sokratum.shared.generated.resources.delete_assert
import sokratum.shared.generated.resources.keyboard_arrow_right
import sokratum.shared.generated.resources.new_chat
import sokratum.shared.generated.resources.new_message
import sokratum.shared.generated.resources.no_internet_connection
import sokratum.shared.generated.resources.quick_chat
import sokratum.shared.generated.resources.save
import sokratum.shared.generated.resources.settings
import sokratum.shared.generated.resources.space_name
import sokratum.shared.generated.resources.temperature
import kotlin.time.Clock

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreenUI(
    viewModel: ChatScreenViewModel,
    onNavigate: (ChatScreenViewModel.ChatScreenNavigationRoutes) -> Unit
) {
    val uiState =
        viewModel.uiState.collectAsStateWithLifecycle(ChatScreenViewModel.ChatScreenUiState.Loading)
    val isPhone =
        ((DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone))
    when (uiState.value) {
        is ChatScreenViewModel.ChatScreenUiState.Error -> {
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
                    onClick = { onNavigate(ChatScreenViewModel.ChatScreenNavigationRoutes.Back) },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.back),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }

        ChatScreenViewModel.ChatScreenUiState.Loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        is ChatScreenViewModel.ChatScreenUiState.Success -> {
            var modelSettingWindowState by remember { mutableStateOf(false) }
            Column(
               modifier = Modifier.padding(top=15.dp).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                                    onNavigate(ChatScreenViewModel.ChatScreenNavigationRoutes.Back)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.back),
                                contentDescription = "back"
                            )
                        }


                        Text(
                            if (viewModel.chatName.isNotBlank()) viewModel.chatName else stringResource(
                                Res.string.new_chat
                            ),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Box(
                            Modifier.size(56.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceContainer)
                                .clickable {
                                    onNavigate(
                                        ChatScreenViewModel.ChatScreenNavigationRoutes.ChatSettings(
                                            chatId = viewModel.chatId.toInt()
                                        )
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.settings),
                                contentDescription = "settings",
                            )
                        }


                    }
                    val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                    val messages =
                        (uiState.value as ChatScreenViewModel.ChatScreenUiState.Success).items
                    var currentDate = messages.firstOrNull()?.sendTime ?: now
                    val listState =
                        rememberLazyListState(if (messages.isNotEmpty()) messages.size - 1 else 0)


                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.weight(1f),
                        state = listState
                    ) {
                        items(messages) { message ->
                            if (message.isModel) {
                                val layout = MDParse.mdParser(message.text, MaterialTheme.typography.bodyMedium,
                                    MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.surfaceContainer)
                                for(x in layout){
                                    x()
                                }
                            } else {
                                var time: String? = null
                                if (message.sendTime != null) {
                                    val h = message.sendTime.hour.toString().padStart(2, '0')
                                    val m = message.sendTime.minute.toString().padStart(2, '0')
                                    time = "$h:$m"

                                    if (message.sendTime.date != currentDate.date) {
                                        val day = message.sendTime.day.toString().padStart(2, '0')
                                        val month =
                                            message.sendTime.month.number.toString()
                                                .padStart(2, '0')
                                        val year = message.sendTime.year
                                        Box(
                                            Modifier.fillMaxWidth(),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "$day.$month.$year",
                                                style = MaterialTheme.typography.bodyLarge
                                            )
                                        }
                                        currentDate = message.sendTime
                                    }

                                }
                                MessageUI(
                                    date = if (time != null) {
                                        {
                                            Text(
                                                time,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.surface
                                                )
                                            )
                                        }
                                    } else null
                                ) {
                                    Text(
                                        message.text,
                                        style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.surface)
                                    )
                                }
                            }

                        }
                    }
                    if ((uiState.value as ChatScreenViewModel.ChatScreenUiState.Success).e == null || (uiState.value as ChatScreenViewModel.ChatScreenUiState.Success).e == "send") {
                        var query by remember { mutableStateOf("") }
                        MessageBar(
                            query = query,
                            onQueryChange = { query=it },
                            onSend = {
                                    viewModel.request(query)
                                query=""
                            },
                            placeholder = stringResource(Res.string.new_message),
                            leadingIcon = {
                                IconButton(
                                    onClick = {modelSettingWindowState=true}
                                ){
                                    Icon(painterResource(Res.drawable.settings), contentDescription = "Model settings")
                                }
                            },
                            trailingIcon = {
                                Icon(
                                    painter = painterResource(Res.drawable.keyboard_arrow_right),
                                    contentDescription = "send"
                                )
                            },
                            singleLine = false,
                            maxLines = 4,
                            modifier = Modifier.padding(bottom = 15.dp).heightIn(min = 56.dp)
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
                                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.onErrorContainer),
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        }

                    }
                }
            }
            if(modelSettingWindowState){
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    AlertDialog(
                        dismissButton = null,
                        onDismissRequest = {},
                        title = {
                            Text(
                                stringResource(Res.string.are_you_sure),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        text = {
                            Column(
                               horizontalAlignment = Alignment.CenterHorizontally,
                                //verticalArrangement = Arrangement.spacedBy()
                            ) {
                                TextFieldCustom(
                                    value = viewModel.temperature,
                                    onValueChange = {it ->
                                        viewModel.temperature=it
                                        it.toDoubleOrNull()?.let { viewModel.digitTemperature=it }
                                    },
                                    label = {Text(
                                        stringResource(Res.string.temperature),
                                        style = MaterialTheme.typography.bodySmall
                                    )}
                                )
                                for(model in AIModels.modelList){
                                    Column(
                                    ){
                                        Row(Modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                model.name,
                                                style = MaterialTheme.typography.bodyLarge,
                                                modifier = Modifier.weight(1f)
                                            )
                                            RadioButton(
                                                selected = AIModels.actualModel!=null && AIModels.actualModel!!.id==model.id,
                                                onClick = { AIModels.actualModel=model}
                                            )
                                        }
                                        Box(Modifier.fillMaxWidth().height(1.dp).background(
                                            MaterialTheme.colorScheme.onSurface))
                                    }
                                }
                            }
                        },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.setTemperature()
                                modelSettingWindowState=false
                            }) {
                                Text(stringResource(Res.string.save))
                            }

                        }
                    )
                }
            }

        }
    }

}