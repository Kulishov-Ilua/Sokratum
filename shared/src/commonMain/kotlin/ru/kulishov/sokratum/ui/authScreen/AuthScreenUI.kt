package ru.kulishov.sokratum.ui.authScreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import io.ktor.http.HttpStatusCode
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.TextFieldCustom
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.bad_key
import sokratum.shared.generated.resources.device_name
import sokratum.shared.generated.resources.empty_key
import sokratum.shared.generated.resources.empty_login
import sokratum.shared.generated.resources.empty_password
import sokratum.shared.generated.resources.hello
import sokratum.shared.generated.resources.incorrect_password
import sokratum.shared.generated.resources.key
import sokratum.shared.generated.resources.know_each_other
import sokratum.shared.generated.resources.log_in
import sokratum.shared.generated.resources.password
import sokratum.shared.generated.resources.password_confirm
import sokratum.shared.generated.resources.password_do_not_match
import sokratum.shared.generated.resources.registration
import sokratum.shared.generated.resources.sign_up
import sokratum.shared.generated.resources.sokratumicon

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AuthScreenUI(
    viewModel: AuthScreenViewModel,
    authSuccess: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    if (uiState.value == AuthScreenViewModel.AuthScreenState.AuthSuccesss) {
        authSuccess()
    }
    Box(
        Modifier.padding(bottom = if (DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone) 80.dp else 0.dp)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(25.dp),
            modifier = Modifier.widthIn(max = 360.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.sokratumicon),
                    contentDescription = "Hello!",
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = when (uiState.value) {
                        AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> stringResource(
                            Res.string.hello
                        )

                        AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> stringResource(
                            Res.string.know_each_other
                        )
                    },
                    style = MaterialTheme.typography.titleLargeEmphasized
                )
            }
            when (uiState.value) {
                AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> {
                    LoadingIndicator()
                }

                else -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(15.dp)
                    ) {
                        when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput -> {
                                val emptyKey =
                                    uiState.value is AuthScreenViewModel.AuthScreenState.BadRegistrationData && viewModel.key.isEmpty()
                                val badKey =
                                    uiState.value is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest && (uiState.value as AuthScreenViewModel.AuthScreenState.BadRegistrationRequest).type == HttpStatusCode.Locked
                                TextFieldCustom(
                                    value = viewModel.key,
                                    onValueChange = { viewModel.key = it },
                                    visualTransformation = PasswordVisualTransformation(),
                                    isError = emptyKey || badKey,
                                    supportingText = if (emptyKey || badKey) {
                                        {
                                            Text(
                                                if (emptyKey) stringResource(Res.string.empty_key)
                                                else stringResource(Res.string.bad_key),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            )
                                        }

                                    } else null,
                                    label = {
                                        Text(
                                            stringResource(Res.string.key),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()
                                )
                            }

                            else -> {}
                        }
                        val emptyLogin =
                            uiState.value is AuthScreenViewModel.AuthScreenState.BadRegistrationData && viewModel.login.isEmpty() || uiState.value is AuthScreenViewModel.AuthScreenState.BadAuthData && viewModel.login.isEmpty()
                        val badLogin =
                            uiState.value is AuthScreenViewModel.AuthScreenState.BadAuthRequest
                        TextFieldCustom(
                            value = viewModel.login,
                            onValueChange = { viewModel.login = it },
                            isError = emptyLogin || badLogin,
                            supportingText = if (emptyLogin) {
                                {
                                    Text(
                                        text = stringResource(Res.string.empty_login),
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                                    )
                                }

                            } else null,
                            label = {
                                Text(
                                    stringResource(Res.string.device_name),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()
                        )

                        val emptyPassword =
                            uiState.value is AuthScreenViewModel.AuthScreenState.BadRegistrationData && viewModel.password.isEmpty() || uiState.value is AuthScreenViewModel.AuthScreenState.BadAuthData && viewModel.password.isEmpty()
                        val incorrectPasswordDuplicate =
                            uiState.value is AuthScreenViewModel.AuthScreenState.BadRegistrationData && viewModel.password != viewModel.passwordDuplicate
                        TextFieldCustom(
                            value = viewModel.password,
                            onValueChange = { viewModel.password = it },
                            isError = emptyPassword || badLogin || incorrectPasswordDuplicate,
                            supportingText = if (emptyPassword || badLogin) {
                                {
                                    Text(
                                        text = if (badLogin) stringResource(Res.string.incorrect_password)
                                        else stringResource(Res.string.empty_password),
                                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.error)
                                    )
                                }

                            } else null,
                            label = {
                                Text(
                                    stringResource(Res.string.password),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()
                        )
                        when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput -> {

                                TextFieldCustom(
                                    value = viewModel.passwordDuplicate,
                                    onValueChange = { viewModel.passwordDuplicate = it },
                                    isError = incorrectPasswordDuplicate,
                                    supportingText = if (incorrectPasswordDuplicate) {
                                        {
                                            Text(
                                                stringResource(Res.string.password_do_not_match),
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.error
                                                )
                                            )
                                        }

                                    } else null,
                                    label = {
                                        Text(
                                            stringResource(Res.string.password_confirm),
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    },
                                    modifier = Modifier.heightIn(min = 56.dp).fillMaxWidth()
                                )
                            }

                            else -> {}
                        }
                        if (!(DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(15.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        when (uiState.value) {
                                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> {
                                                viewModel.goToRegistration()
                                            }

                                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> {
                                                viewModel.goToAuth()
                                            }
                                        }
                                    },
                                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                    modifier = Modifier.widthIn(min = 150.dp)
                                ) {
                                    Text(
                                        text = when (uiState.value) {
                                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> stringResource(
                                                Res.string.registration
                                            )

                                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> stringResource(
                                                Res.string.log_in
                                            )
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                                    )
                                }

                                Button(
                                    onClick = {
                                        when (uiState.value) {
                                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> {
                                                viewModel.auth()
                                            }

                                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> {
                                                viewModel.registration()
                                            }
                                        }
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                                    modifier = Modifier.widthIn(min = 150.dp)
                                ) {
                                    Text(
                                        text = when (uiState.value) {
                                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> stringResource(
                                                Res.string.log_in
                                            )

                                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> stringResource(
                                                Res.string.sign_up
                                            )
                                        },
                                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                                    )
                                }

                            }
                        }
                    }
                }
            }

        }
    }
    if ((DeviceData.actualPlatform == null || DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone)) {
        Box(
            Modifier.padding(bottom = 25.dp).fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> {
                                viewModel.goToRegistration()
                            }

                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> {
                                viewModel.goToAuth()
                            }
                        }
                    },
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    modifier = Modifier.widthIn(min = 150.dp)
                ) {
                    Text(
                        text = when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> stringResource(
                                Res.string.registration
                            )

                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> stringResource(
                                Res.string.log_in
                            )
                        },
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary)
                    )
                }

                Button(
                    onClick = {
                        when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> {
                                viewModel.auth()
                            }

                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> {
                                viewModel.registration()
                            }
                        }
                    },
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    modifier = Modifier.widthIn(min = 150.dp)
                ) {
                    Text(
                        text = when (uiState.value) {
                            AuthScreenViewModel.AuthScreenState.AuthInput, AuthScreenViewModel.AuthScreenState.AuthLoading, AuthScreenViewModel.AuthScreenState.AuthSuccesss, AuthScreenViewModel.AuthScreenState.BadAuthData, is AuthScreenViewModel.AuthScreenState.BadAuthRequest -> stringResource(
                                Res.string.log_in
                            )

                            AuthScreenViewModel.AuthScreenState.BadRegistrationData, is AuthScreenViewModel.AuthScreenState.BadRegistrationRequest, AuthScreenViewModel.AuthScreenState.RegistrationInput, AuthScreenViewModel.AuthScreenState.RegistrationLoading -> stringResource(
                                Res.string.sign_up
                            )
                        },
                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                    )
                }

            }
        }

    }
}