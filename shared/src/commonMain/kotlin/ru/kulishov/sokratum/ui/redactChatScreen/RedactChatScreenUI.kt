package ru.kulishov.sokratum.ui.redactChatScreen

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.TextFieldCustom
import ru.kulishov.sokratum.ui.elements.UpsElement
import ru.kulishov.sokratum.ui.redactSpaceScreen.RedactSpaceScreenViewModel
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.are_you_sure
import sokratum.shared.generated.resources.back
import sokratum.shared.generated.resources.cancel
import sokratum.shared.generated.resources.chat_name
import sokratum.shared.generated.resources.create
import sokratum.shared.generated.resources.creation_of_chat
import sokratum.shared.generated.resources.creation_of_space
import sokratum.shared.generated.resources.delete
import sokratum.shared.generated.resources.delete_assert
import sokratum.shared.generated.resources.redaction_of_chat
import sokratum.shared.generated.resources.redaction_of_space
import sokratum.shared.generated.resources.save
import sokratum.shared.generated.resources.space_name
import sokratum.shared.generated.resources.system_prompt

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RedactChatScreenUI(
    viewModel: RedactChatScreenViewModel,
    onNavigate: (RedactChatScreenViewModel.RedactChatScreenNavigationRoutes) -> Unit
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    when (uiState.value) {
        is RedactChatScreenViewModel.RedactChatScreenUiState.Error -> {
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
                    onClick = { onNavigate(RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Back) },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = stringResource(Res.string.back),
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                    )
                }
            }
        }

        RedactChatScreenViewModel.RedactChatScreenUiState.Loading -> {
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                LoadingIndicator()
            }
        }

        is RedactChatScreenViewModel.RedactChatScreenUiState.Navigation -> {
            val route =
                (uiState.value as RedactChatScreenViewModel.RedactChatScreenUiState.Navigation).route
            onNavigate(route)
        }

        RedactChatScreenViewModel.RedactChatScreenUiState.Success -> {
            var deleteDialogState by remember { mutableStateOf(false) }
            Column(
                Modifier.padding(top = 15.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier.widthIn(max = 825.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        IconButton(
                            onClick = {
                                onNavigate(RedactChatScreenViewModel.RedactChatScreenNavigationRoutes.Back)
                            }
                        ) {
                            Icon(painterResource(Res.drawable.back), contentDescription = "back")
                        }
                        Text(
                            stringResource(if (viewModel.chatId < 0) Res.string.creation_of_chat else Res.string.redaction_of_chat),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.weight(1f)
                        )
                        if (viewModel.chatId >= 0) {
                            IconButton(
                                onClick = {
                                    deleteDialogState = true
                                }
                            ) {
                                Icon(
                                    painterResource(Res.drawable.delete),
                                    contentDescription = "delete",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }

                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(15.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        TextFieldCustom(
                            value = viewModel.name,
                            onValueChange = { viewModel.name = it },
                            label = {
                                Text(
                                    stringResource(Res.string.chat_name),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        )
                        TextFieldCustom(
                            value = viewModel.systemPrompt,
                            onValueChange = { viewModel.systemPrompt = it },
                            label = {
                                Text(
                                    stringResource(Res.string.system_prompt),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.heightIn(min = 200.dp).fillMaxWidth()
                        )
                    }
                    Column(
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 15.dp)
                    ) {
                        Row {
                            Button(
                                onClick = { viewModel.redact() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    stringResource(if (viewModel.spaceId < 0) Res.string.create else Res.string.save),
                                    style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                                )
                            }

                            //Text(text = )
                        }
                    }

                }
            }
            if (deleteDialogState) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    AlertDialog(
                        onDismissRequest = { deleteDialogState = false },
                        title = {
                            Text(
                                stringResource(Res.string.are_you_sure),
                                style = MaterialTheme.typography.headlineSmall
                            )
                        },
                        text = { Text(stringResource(Res.string.delete_assert)) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.delete()
                            }) {
                                Text(stringResource(Res.string.delete))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { deleteDialogState = false }) {
                                Text(stringResource(Res.string.cancel))
                            }
                        },
                    )
                }
            }
        }
    }
}
