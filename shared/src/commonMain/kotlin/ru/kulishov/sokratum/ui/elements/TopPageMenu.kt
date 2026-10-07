package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.back

@Composable
fun TopPageMenu(
    elementColor: Color = MaterialTheme.colorScheme.onSurface,
    text: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.padding(vertical = 12.dp).fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(48.dp).clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.back),
                    contentDescription = "back",
                    tint = elementColor
                )
            }
            if (!text.isNullOrEmpty()) {
                Text(
                    text,
                    style = MaterialTheme.typography.titleLarge.copy(color = elementColor),
                    modifier = Modifier.widthIn(max = 230.dp)
                )
            }
            if (trailingIcon != null) {
                Box(
                    Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    trailingIcon()
                }
            }
        }
    }
}