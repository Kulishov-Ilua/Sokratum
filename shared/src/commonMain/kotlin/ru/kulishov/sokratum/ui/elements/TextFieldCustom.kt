package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun TextFieldCustom(
    value: String,
    onValueChange: (String) -> Unit,
    height: Int = 60,
    modifier: Modifier = Modifier.fillMaxWidth().height(height.dp),
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    prefix: @Composable (() -> Unit)? = null,
    suffix: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    minLines: Int = 1,
    interactionSource: MutableInteractionSource? = null,
    shape: Shape = RoundedCornerShape(10),
    isTransparent: Boolean = false,
    colors: TextFieldColors = TextFieldDefaults.colors(
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        errorTextColor = MaterialTheme.colorScheme.error,
        focusedContainerColor = if(!isTransparent) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
        unfocusedContainerColor = if(!isTransparent) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
        disabledContainerColor = if(!isTransparent) MaterialTheme.colorScheme.surfaceContainer else Color.Transparent,
        errorContainerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
        cursorColor = MaterialTheme.colorScheme.primary,
        errorCursorColor = MaterialTheme.colorScheme.error,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        errorIndicatorColor = Color.Transparent,
        focusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
        unfocusedLeadingIconColor = MaterialTheme.colorScheme.onSurface,
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurface,
        errorLeadingIconColor = MaterialTheme.colorScheme.error,
        focusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface,
        errorTrailingIconColor = MaterialTheme.colorScheme.error,
        focusedLabelColor = MaterialTheme.colorScheme.onSurface,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
        disabledLabelColor = MaterialTheme.colorScheme.onSurface,
        errorLabelColor = MaterialTheme.colorScheme.error,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurface,
        errorPlaceholderColor = MaterialTheme.colorScheme.error,
        focusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedSupportingTextColor = MaterialTheme.colorScheme.onSurface,
        disabledSupportingTextColor = MaterialTheme.colorScheme.onSurface,
        errorSupportingTextColor = MaterialTheme.colorScheme.error,
        focusedPrefixColor = MaterialTheme.colorScheme.onSurface,
        unfocusedPrefixColor = MaterialTheme.colorScheme.onSurface,
        disabledPrefixColor = MaterialTheme.colorScheme.onSurface,
        errorPrefixColor = MaterialTheme.colorScheme.error,
        focusedSuffixColor = MaterialTheme.colorScheme.onSurface,
        unfocusedSuffixColor = MaterialTheme.colorScheme.onSurface,
        disabledSuffixColor = MaterialTheme.colorScheme.onSurface,
        errorSuffixColor = MaterialTheme.colorScheme.error
    ),
    textStyle: TextStyle = MaterialTheme.typography.titleSmall.copy(color = if (!isError) colors.focusedTextColor else colors.errorTextColor),
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle = textStyle,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        prefix = prefix,
        suffix = suffix,
        supportingText = supportingText,
        isError = isError,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        minLines = minLines,
        interactionSource = interactionSource,
        shape = shape,
        colors = colors,
    )
}