package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun MessageBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSend: () -> Unit = {},
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    maxLines:Int = 4,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth().widthIn(max = 400.dp),
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        leadingIcon = leadingIcon,
        trailingIcon = if (trailingIcon != null) {
            {
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onSend()
                    }) {
                        trailingIcon()
                    }
                }

            }
        } else null,
        singleLine = singleLine,
        minLines=1,
        maxLines= if(singleLine) 1 else maxLines,
        shape = CircleShape,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent,
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
        ),
    )
}