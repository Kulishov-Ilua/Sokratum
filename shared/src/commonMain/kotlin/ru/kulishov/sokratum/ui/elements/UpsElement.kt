package ru.kulishov.sokratum.ui.elements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import sokratum.shared.generated.resources.Res
import sokratum.shared.generated.resources.o_no
import sokratum.shared.generated.resources.something_went_wrong

@Composable
fun UpsElement(
    title:String = stringResource(Res.string.something_went_wrong),
    description: String? = null
){
    Column(horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(painter = painterResource(Res.drawable.o_no),
            contentDescription = "", tint = MaterialTheme.colorScheme.onSurface)
        Text(title,
            style = MaterialTheme.typography.headlineSmall)
        if(!description.isNullOrBlank()){
            Text(description,
                style = MaterialTheme.typography.bodyMedium)
        }

    }
}