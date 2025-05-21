package com.huyhieu.library.ui.dialog.base

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.huyhieu.library.ui.common.AppButtonNegative
import com.huyhieu.library.ui.common.AppButtonPositive
import com.huyhieu.library.ui.theme.Sky
import kotlinx.coroutines.launch

@Composable
fun ConfirmDialog(
    title: String = "",
    content: String = "",
    addStyles: (AnnotatedString.Builder.() -> Unit)? = null,
    confirmLabel: String = "OK",
    confirmContainerColor: Color = Sky,
    cancelLabel: String? = null,
    image: @Composable (ColumnScope.() -> Unit)? = null,
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {},
    onCancel: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    AppDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        image?.invoke(this)

        Text(
            title,
            style = TextStyle(
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
            ),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))

        Text(
            text = AnnotatedString.Builder(content).apply {
                addStyles?.invoke(this)
            }.toAnnotatedString(),
            textAlign = TextAlign.Center,
        )

        AppButtonPositive(
            name = confirmLabel,
            containerColor = confirmContainerColor,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
                .navigationBarsPadding(),
        ) {
            scope.launch {
                onConfirm()
            }
        }

        cancelLabel?.also {
            AppButtonNegative(
                name = cancelLabel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 24.dp)
                    .navigationBarsPadding(),
            ) {
                scope.launch {
                    onCancel?.invoke()
                }
            }
        }
    }
}