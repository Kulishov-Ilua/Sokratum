package ru.kulishov.sokratum.ui.elements

import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable

@Composable
fun TextFieldCustom(
    text: String,
    onValueChange:(String)->Unit,
    isError:((String)-> Boolean)? = null,

){
    TextField(
        value = text,
        onValueChange = onValueChange
    )
}