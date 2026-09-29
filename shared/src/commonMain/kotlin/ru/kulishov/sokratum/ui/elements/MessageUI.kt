package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun MessageUI(
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.surface,
    mode: Boolean = true,
    author: (@Composable () -> Unit)? = null,
    date:  (@Composable () -> Unit)? = null,
    content: (@Composable (contentColor: Color) -> Unit)? = null,
) {
    Box(Modifier.fillMaxWidth(),
        contentAlignment = if(mode) Alignment.CenterEnd else Alignment.CenterStart){
        Box(
            Modifier.clip(
                RoundedCornerShape(
                    topStart = 20f,
                    topEnd = 20f,
                    bottomStart = if(mode) 20f else 4f,
                    bottomEnd = if(mode) 4f else 20f
                )
            ).background(if(mode) backgroundColor else backgroundColor)
        ) {
            Column(Modifier.padding(vertical = 5.dp, horizontal = 15.dp)) {
                if(author!=null){
                    author()
                }
                if (content != null) {
                    content(contentColor)
                }
                if(date!=null){
                    date()
                }

            }
        }
    }

}