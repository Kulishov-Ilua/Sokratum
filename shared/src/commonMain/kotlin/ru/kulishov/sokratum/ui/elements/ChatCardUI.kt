package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.number
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.chat
import sokratum.shared.generated.resources.space
import sokratum.shared.generated.resources.yesterday

@Composable
fun ChatCardUI(
    name: String,
    spaceName: String? = null,
    lastOpen: LocalDateTime? = null,
    now: LocalDateTime? = null,
    onClick:(()->Unit)? = null
) {
    Card(
        Modifier.fillMaxWidth().height(62.dp).background(
            MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(5.dp)
        ).clickable{
            if(onClick!=null){
                onClick()
            }
        }
    ) {
        Row(
            Modifier.padding(
                start = 16.dp,
                end =  16.dp,
                top = 8.dp,
                bottom = 8.dp
            ).fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(Res.drawable.chat), contentDescription = "Chat",
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
                if (spaceName != null) {
                    Row {
                        Text(
                            stringResource(Res.string.space) + ": ",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            spaceName,
                            style = MaterialTheme.typography.bodyLarge,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

            }
            if (lastOpen != null) {
                val time = if (now != null) {
                    when {
                        lastOpen.date == now.date -> {
                            val h = lastOpen.hour.toString().padStart(2, '0')
                            val m = lastOpen.minute.toString().padStart(2, '0')
                            "$h:$m"
                        }

                        lastOpen.date == now.date.minus(1, DateTimeUnit.DAY) -> {
                            stringResource(Res.string.yesterday)
                        }

                        else -> {
                            val day = lastOpen.day.toString().padStart(2, '0')
                            val month = lastOpen.month.number.toString().padStart(2, '0')
                            val year = lastOpen.year
                            "$day.$month.$year"
                        }
                    }
                } else {
                    val day = lastOpen.day.toString().padStart(2, '0')
                    val month = lastOpen.month.number.toString().padStart(2, '0')
                    val year = lastOpen.year
                    "$day.$month.$year"
                }
                Text(time, style = MaterialTheme.typography.bodySmall)
            }
        }


    }
}