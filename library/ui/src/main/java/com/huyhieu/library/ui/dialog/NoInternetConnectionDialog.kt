package com.huyhieu.library.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.huyhieu.library.ui.R
import com.huyhieu.library.ui.dialog.base.ConfirmDialog

@Composable
fun NoInternetConnectionDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    ConfirmDialog(
        title = "Connection failed",
        content = "Please check your Internet connection and try again!",
        confirmLabel = "OK, I got it!",
        confirmContainerColor = Color.Red,
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        image = {
            Box(
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .size(100.dp)
                    .background(Color.Red.copy(0.15F), shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_wifi_slash),
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(54.dp),
                    tint = Color.Red,
                )
            }
        },
    )
}