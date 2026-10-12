package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.folder
import sokratum.shared.generated.resources.more_vert

@Composable
fun SpaceCardUI(
    name: String,
    onRedact: (() -> Unit)? = null,
    onClick: (() -> Unit)?
) {
    Card(
        Modifier.fillMaxWidth().height(56.dp).background(
            MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(5.dp)
        ).clickable{
            if(onClick!=null){
                onClick()
            }
        }
    ) {
        Row(
            Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.folder), contentDescription = "space",
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)
            )
            Text(
                name, style = MaterialTheme.typography.bodyLarge, overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (onRedact != null) {
                Icon(
                    painterResource(Res.drawable.more_vert), contentDescription = "Setup",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable {
                        onRedact()
                    })

            }
        }


    }
}