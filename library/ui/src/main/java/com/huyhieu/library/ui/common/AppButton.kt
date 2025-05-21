package com.huyhieu.library.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.huyhieu.library.ui.theme.Sky


@Composable
fun AppButtonPositive(
    name: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    containerColor: Color = Sky,
    contentColor: Color = Color.White,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold),
    contentPadding: PaddingValues = PaddingValues(14.dp),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        Text(name, style = textStyle)
    }
}

@Composable
fun AppButtonNegative(
    name: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    containerColor: Color = Color.White,
    contentColor: Color = Sky,
    enabled: Boolean = true,
    textStyle: TextStyle = TextStyle(fontWeight = FontWeight.SemiBold),
    contentPadding: PaddingValues = PaddingValues(14.dp),
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
        ),
        contentPadding = contentPadding,
        modifier = modifier,
    ) {
        Text(name, style = textStyle)
    }
}