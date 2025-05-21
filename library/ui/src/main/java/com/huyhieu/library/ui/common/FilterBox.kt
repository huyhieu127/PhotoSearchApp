package com.huyhieu.library.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.huyhieu.library.ui.shared.DataProvider
import com.huyhieu.library.ui.theme.Sky

@Preview
@Composable
fun FilterBox(
    modifier: Modifier = Modifier,
    orientation: Int = -1,
    size: Int = -1,
    color: Int = -1,
    onOrientationClick: () -> Unit = {},
    onSizeClick: () -> Unit = {},
    onColorClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .weight(1F)
                .border(1.dp, if (orientation == -1) Color.LightGray else Sky, RoundedCornerShape(10.dp))
                .fillMaxHeight()
                .clickable {
                    onOrientationClick()
                }
                .padding(start = 8.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (orientation == -1) "Orientation" else DataProvider.listOrientations[orientation],
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1F)
            )
            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = null,
            )
        }
        Row(
            modifier = Modifier
                .weight(1F)
                .border(1.dp, if (size == -1) Color.LightGray else Sky, RoundedCornerShape(10.dp))
                .fillMaxHeight()
                .clickable {
                    onColorClick()
                }
                .padding(start = 8.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                if (size == -1) "Size" else DataProvider.listSizes[size],
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1F)
            )
            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = null,
            )
        }
        Row(
            modifier = Modifier
                .weight(1F)
                .border(1.dp, if (color == -1) Color.LightGray else Sky, RoundedCornerShape(10.dp))
                .fillMaxHeight()
                .clickable {
                    onColorClick()
                }
                .padding(start = 8.dp, end = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(
                        (if (color == -1)
                            Color.DarkGray
                        else
                            Color("#${DataProvider.listColors[color]}".toColorInt())),
                        CircleShape
                    )
            )
            Spacer(Modifier.width(6.dp))
            Text(
                if (color == -1) "Color" else "#${DataProvider.listColors[color]}",
                fontSize = 13.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                modifier = Modifier.weight(1F)
            )
            Icon(
                Icons.Rounded.ArrowDropDown,
                contentDescription = null,
            )
        }
    }
}