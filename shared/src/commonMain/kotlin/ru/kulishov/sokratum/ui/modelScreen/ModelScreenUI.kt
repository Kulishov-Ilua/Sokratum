package ru.kulishov.sokratum.ui.modelScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.animateSizeAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import ru.kulishov.sokratum.ui.DeviceData
import ru.kulishov.sokratum.ui.elements.MessageUI
import ru.kulishov.sokratum.ui.elements.TopPageMenu
import ru.kulishov.sokratum.ui.elements.UpsElement
import ru.kulishov.sokratum.ui.navigation.NavigationRoutes
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.back_to_model_settings
import sokratum.shared.generated.resources.check_details
import sokratum.shared.generated.resources.connect_model
import sokratum.shared.generated.resources.connecting_the_model
import sokratum.shared.generated.resources.continue_training
import sokratum.shared.generated.resources.hello
import sokratum.shared.generated.resources.hello_before
import sokratum.shared.generated.resources.help
import sokratum.shared.generated.resources.help_outline
import sokratum.shared.generated.resources.model_key
import sokratum.shared.generated.resources.model_key_description
import sokratum.shared.generated.resources.model_key_if_nesessary
import sokratum.shared.generated.resources.model_name
import sokratum.shared.generated.resources.model_name_description
import sokratum.shared.generated.resources.model_timeout
import sokratum.shared.generated.resources.model_timeout_description
import sokratum.shared.generated.resources.model_timeout_placeholder
import sokratum.shared.generated.resources.model_url
import sokratum.shared.generated.resources.model_url_description
import sokratum.shared.generated.resources.person
import sokratum.shared.generated.resources.skip
import sokratum.shared.generated.resources.sokratumicon
import sokratum.shared.generated.resources.specify_model_parametr
import sokratum.shared.generated.resources.the_model_is_not_responding

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ModelScreenUI(
    viewModel: ModelScreenViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.navigation.collect { event ->
            when (event) {
                NavigationRoutes.HomeScreen -> navController.navigate(NavigationRoutes.HomeScreen)
                NavigationRoutes.ModelScreen -> TODO()
            }

        }
    }

    val helpWindowAnimateSize = animateIntSizeAsState(
        targetValue = if (viewModel.helpWindow) IntSize(width = 100000, height = 100000)
        else IntSize(width = 150, height = 56),
        animationSpec = tween(durationMillis = 400)
    )

    val uiState = viewModel.uiState.collectAsState()
    Box(
        Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            if (uiState.value == ModelScreenViewModel.LoadScreenState.Loading || uiState.value == ModelScreenViewModel.LoadScreenState.ModelAddHelloMessage) {
                Icon(
                    painter = painterResource(Res.drawable.sokratumicon),
                    contentDescription = "Hello",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            when (uiState.value) {
                is ModelScreenViewModel.LoadScreenState.Error -> UpsElement()
                ModelScreenViewModel.LoadScreenState.Loading -> {
                    LoadingIndicator()
                }
                ModelScreenViewModel.LoadScreenState.LoadingModel -> {
                    LoadingIndicator()
                }
                ModelScreenViewModel.LoadScreenState.ModelAdd -> {
                    Icon(painterResource(Res.drawable.person),
                        contentDescription = "Model parameters",
                        tint = MaterialTheme.colorScheme.onSurface)
                    Text(stringResource(Res.string.specify_model_parametr),
                        style = MaterialTheme.typography.titleLargeEmphasized)
                    TextField(
                        value = viewModel.modelName,
                        onValueChange = {viewModel.modelName=it},
                        label = {Text(stringResource(Res.string.model_name), style = MaterialTheme.typography.bodySmall)},
                        isError = viewModel.modelName.isNullOrBlank()
                    )
                    TextField(
                        value = viewModel.modelUrl,
                        onValueChange = {viewModel.modelUrl=it},
                        label = {Text(stringResource(Res.string.model_url), style = MaterialTheme.typography.bodySmall)},
                        isError = viewModel.modelUrl.isNullOrBlank()
                    )
                    TextField(
                        value = viewModel.modelKey,
                        onValueChange = {viewModel.modelKey=it},
                        label = {Text(stringResource(Res.string.model_key), style = MaterialTheme.typography.bodySmall)},
                        visualTransformation = PasswordVisualTransformation()
                    )
                    TextField(
                        value = viewModel.timeout.toString(),
                        onValueChange = {viewModel.timeout=it.toLong()},
                        label = {Text(stringResource(Res.string.model_timeout), style = MaterialTheme.typography.bodySmall)},
                        isError = viewModel.timeout<0 || viewModel.timeout>5000,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                ModelScreenViewModel.LoadScreenState.ModelAddMessage -> {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopStart){
                        Column(verticalArrangement = Arrangement.spacedBy(15.dp)){
                            MessageUI {
                                Text(viewModel.message,
                                    style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
                ModelScreenViewModel.LoadScreenState.ModelError -> {
                    UpsElement(stringResource(Res.string.the_model_is_not_responding),
                        stringResource(Res.string.check_details))
                }
                ModelScreenViewModel.LoadScreenState.ModelAddHelloMessage -> {
                    Text(
                        text = stringResource(Res.string.hello),
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text(text = stringResource(Res.string.hello_before))
                }
            }
            if (DeviceData.actualPlatform == null || DeviceData.actualPlatform !is DeviceData.Platforms.Phone && DeviceData.actualPlatform !is DeviceData.Platforms.WebPhone) {

            }
        }
    }
    if (DeviceData.actualPlatform != null && (DeviceData.actualPlatform is DeviceData.Platforms.Phone || DeviceData.actualPlatform is DeviceData.Platforms.WebPhone)) {
        Box(
            Modifier.padding(bottom = 30.dp).fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (uiState.value is ModelScreenViewModel.LoadScreenState.ModelAdd) {
                        Button(
                            onClick = {
                                if (!viewModel.helpWindow) {
                                    viewModel.openHelpWindow()
                                }
                            },
                            modifier = Modifier.width(helpWindowAnimateSize.value.width.dp)
                                .height(helpWindowAnimateSize.value.height.dp)
                        ) {
                            if (!viewModel.helpWindow) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(11.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(Res.drawable.help_outline),
                                        contentDescription = "Help",
                                        tint = MaterialTheme.colorScheme.surface
                                    )
                                    Text(
                                        text = stringResource(Res.string.help),
                                        style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface)
                                    )
                                }
                            }

                        }
                    }
                    if (uiState.value is ModelScreenViewModel.LoadScreenState.ModelAddMessage) {
                        Box(
                            Modifier.clip(CircleShape)
                                .border(width = 3.dp, color = MaterialTheme.colorScheme.primary)
                                .clickable {
                                    viewModel.goHome()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(stringResource(Res.string.skip),
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.primary),
                                modifier = Modifier.padding(16.dp))
                        }
                    }
                    if(!viewModel.helpWindow&&uiState.value !is ModelScreenViewModel.LoadScreenState.LoadingModel && uiState.value !is ModelScreenViewModel.LoadScreenState.Loading){
                        Button(
                            onClick = {
                                when(uiState.value){
                                    is ModelScreenViewModel.LoadScreenState.Error -> viewModel.goUiState(
                                        ModelScreenViewModel.LoadScreenState.ModelAdd)
                                    ModelScreenViewModel.LoadScreenState.Loading -> {}
                                    ModelScreenViewModel.LoadScreenState.LoadingModel -> {}
                                    ModelScreenViewModel.LoadScreenState.ModelAdd -> viewModel.addModel()
                                    ModelScreenViewModel.LoadScreenState.ModelAddHelloMessage -> viewModel.goUiState(
                                        ModelScreenViewModel.LoadScreenState.ModelAdd)
                                    ModelScreenViewModel.LoadScreenState.ModelAddMessage -> viewModel.goTutorial()
                                    ModelScreenViewModel.LoadScreenState.ModelError -> viewModel.goUiState(
                                        ModelScreenViewModel.LoadScreenState.ModelAdd)
                                }
                            },
                        ){
                            Text(text = when(uiState.value){
                                is ModelScreenViewModel.LoadScreenState.Error -> stringResource(Res.string.back_to_model_settings)
                                ModelScreenViewModel.LoadScreenState.Loading -> ""
                                ModelScreenViewModel.LoadScreenState.LoadingModel -> ""
                                ModelScreenViewModel.LoadScreenState.ModelAdd -> stringResource(Res.string.connect_model)
                                ModelScreenViewModel.LoadScreenState.ModelAddHelloMessage -> stringResource(Res.string.connect_model)
                                ModelScreenViewModel.LoadScreenState.ModelAddMessage -> stringResource(Res.string.continue_training)
                                ModelScreenViewModel.LoadScreenState.ModelError -> stringResource(Res.string.back_to_model_settings)
                            },
                                style = MaterialTheme.typography.titleMedium.copy(color = MaterialTheme.colorScheme.surface))
                        }
                    }
                }
            }
        }
    }
    if (viewModel.helpWindow) {
        Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                TopPageMenu(
                    elementColor = MaterialTheme.colorScheme.surface,
                    text = stringResource(Res.string.connecting_the_model),
                    onClick = { viewModel.closeHelpWindow() }
                )
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(15.dp)
                ) {
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                stringResource(Res.string.model_name) + ":",
                                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            Text(
                                stringResource(Res.string.model_name_description),
                                style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            TextField(
                                value = "gemma4",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(Res.string.model_name),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                stringResource(Res.string.model_url) + ":",
                                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            Text(
                                stringResource(Res.string.model_url_description),
                                style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            TextField(
                                value = "http://localhost:12434",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(Res.string.model_url),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                stringResource(Res.string.model_key) + ":",
                                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            Text(
                                stringResource(Res.string.model_key_description),
                                style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            TextField(
                                value = "keyexample",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(Res.string.model_key_if_nesessary),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = PasswordVisualTransformation()
                            )
                        }
                    }
                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(
                                stringResource(Res.string.model_timeout) + ":",
                                style = MaterialTheme.typography.titleLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            Text(
                                stringResource(Res.string.model_timeout_description),
                                style = MaterialTheme.typography.bodyLarge.copy(MaterialTheme.colorScheme.surface)
                            )
                            TextField(
                                value = "gemma4",
                                onValueChange = {},
                                readOnly = true,
                                label = {
                                    Text(
                                        stringResource(Res.string.model_timeout_placeholder),
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }
                    }
                }
            }
        }
    }


}