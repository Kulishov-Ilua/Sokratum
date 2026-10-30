package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.kulishov.sokratum.ui.DeviceData
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.computer
import sokratum.shared.generated.resources.smartphone
import sokratum.shared.generated.resources.tablet_mac

@Composable
fun DeviceCardUI(
    device: DeviceData.Platforms,
    login: String,
    os: String? = null
) {
    Card(
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(10.dp))
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            modifier = Modifier.padding(horizontal = 25.dp, vertical = 15.dp)
        ) {
            Icon(
                painter = when (device) {
                    is DeviceData.Platforms.Desktop, is DeviceData.Platforms.WebDesktop -> painterResource(
                        Res.drawable.computer
                    )

                    is DeviceData.Platforms.Phone, is DeviceData.Platforms.WebPhone -> painterResource(
                        Res.drawable.smartphone
                    )

                    is DeviceData.Platforms.Tablet, is DeviceData.Platforms.WebTablet -> painterResource(
                        Res.drawable.tablet_mac
                    )
                }, contentDescription = "device",
                modifier = Modifier.size(32.dp)
            )
            Column {
                Text(login, style = MaterialTheme.typography.titleMediumEmphasized)
                if (os != null) {
                    Text(os, style = MaterialTheme.typography.bodyMedium)
                }

            }

        }
    }
}