package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
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
import ru.kulishov.sokratum.domain.model.File
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.chat
import sokratum.shared.generated.resources.delete
import sokratum.shared.generated.resources.insert_drive_file
import sokratum.shared.generated.resources.space
import sokratum.shared.generated.resources.uploaded
import sokratum.shared.generated.resources.yesterday

@Composable
fun FileCardUI(
    fileName: String,
    loadTime: LocalDateTime?,
    now: LocalDateTime?,
    onDelete:()-> Unit
){
    Card(
        Modifier.fillMaxWidth().height(62.dp).background(
            MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(5.dp)
        )
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
                painter = painterResource(Res.drawable.insert_drive_file), contentDescription = "Chat",
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    fileName,
                    style = MaterialTheme.typography.bodyLarge,
                    overflow = TextOverflow.Ellipsis
                )
                if (loadTime != null && now!=null) {
                    val time = if (now != null) {
                        when {
                            loadTime.date == now.date -> {
                                val h = loadTime.hour.toString().padStart(2, '0')
                                val m = loadTime.minute.toString().padStart(2, '0')
                                "$h:$m"
                            }

                            loadTime.date == now.date.minus(1, DateTimeUnit.DAY) -> {
                                stringResource(Res.string.yesterday)
                            }

                            else -> {
                                val day = loadTime.day.toString().padStart(2, '0')
                                val month = loadTime.month.number.toString().padStart(2, '0')
                                val year = loadTime.year
                                "$day.$month.$year"
                            }
                        }
                    } else {
                        val day = loadTime.day.toString().padStart(2, '0')
                        val month = loadTime.month.number.toString().padStart(2, '0')
                        val year = loadTime.year
                        "$day.$month.$year"
                    }
                    Row {
                        Text(
                            stringResource(Res.string.uploaded) + ": ",
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            time,
                            style = MaterialTheme.typography.bodyLarge,

                        )
                    }
                }

            }
            IconButton(
                onClick = {onDelete()}
            ){
                Icon(painterResource(Res.drawable.delete), tint = MaterialTheme.colorScheme.error,
                    contentDescription = "delete")
            }

        }


    }
}