package com.huyhieu.library.ui.bottom_sheet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.huyhieu.library.ui.bottom_sheet.base.AppBottomSheetSimple
import com.huyhieu.library.ui.common.AppButtonNegative
import com.huyhieu.library.ui.common.AppButtonPositive
import com.huyhieu.library.ui.shared.DataProvider
import com.huyhieu.library.ui.theme.Sky

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheet(
    orientation: Int,
    size: Int,
    color: Int,
    onDismiss: () -> Unit,
    onApplyChanges: (Int, Int, Int) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var orientationSelected by remember(orientation) { mutableIntStateOf(orientation) }
    var sizeSelected by remember(size) { mutableIntStateOf(size) }
    var colorSelected by remember(color) { mutableIntStateOf(size) }
    AppBottomSheetSimple(
        title = "Filters",
        sheetState = sheetState,
        onDismissRequest = onDismiss,
    ) {
        FilterType(
            name = "Orientations",
            itemList = DataProvider.listOrientations,
            index = orientation,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 24.dp),
            onItemClick = {
                orientationSelected = it
            },
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 12.dp)
                .background(
                    Color.LightGray.copy(0.2F),
                ),
        )
        FilterType(
            name = "Sizes",
            itemList = DataProvider.listSizes,
            index = size,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 24.dp),
            onItemClick = {
                sizeSelected = it
            },
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .padding(horizontal = 12.dp)
                .background(
                    Color.LightGray.copy(0.2F),
                ),
        )
        FilterColor(
            name = "Colors",
            itemList = DataProvider.listColors,
            index = color,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 24.dp),
            onItemClick = {
                colorSelected = it
            },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding(),
        ) {
            AppButtonNegative(
                modifier = Modifier.weight(1F),
                name = "Reset default",
            ) {
                orientationSelected = -1
                sizeSelected = -1
                colorSelected = -1
                onApplyChanges(orientationSelected, sizeSelected, colorSelected)
            }
            Spacer(Modifier.width(8.dp))
            AppButtonPositive(
                modifier = Modifier.weight(1F),
                name = "Apply changes",
            ) {
                onApplyChanges(orientationSelected, sizeSelected, colorSelected)
            }
        }
    }
}

@Composable
fun FilterType(
    name: String,
    modifier: Modifier = Modifier,
    index: Int = -1,
    itemList: List<String> = emptyList(),
    onItemClick: (Int) -> Unit = {},
) {
    var indexSelected by remember(index) { mutableIntStateOf(index) }
    Column(
        modifier = modifier
    ) {
        Text(
            name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            (itemList).onEachIndexed { index, s ->
                Text(
                    text = s,
                    style = TextStyle(
                        color = if (index == indexSelected) Color.White else Color.Black,
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable {
                            indexSelected = if (index == indexSelected) {
                                -1
                            } else {
                                index
                            }
                            onItemClick(indexSelected)
                        }
                        .then(
                            if (index == indexSelected) {
                                Modifier.background(Sky)
                            } else {
                                Modifier.border(1.dp, Color.LightGray, CircleShape)
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
fun FilterColor(
    name: String,
    modifier: Modifier = Modifier,
    index: Int = -1,
    itemList: List<String> = emptyList(),
    onItemClick: (Int) -> Unit = {},
) {
    var indexSelected by remember { mutableIntStateOf(index) }
    Column(
        modifier = modifier
    ) {
        Text(
            name,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
            ),
        )
        Spacer(Modifier.height(12.dp))
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxWidth(),
            columns = StaggeredGridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalItemSpacing = 8.dp,
        ) {
            itemsIndexed(itemList) { index, it ->
                Text(
                    text = "#$it",
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .clickable {
                            indexSelected = if (index == indexSelected) {
                                -1
                            } else {
                                index
                            }
                            onItemClick(indexSelected)
                        }
                        .background(Color("#$it".toColorInt()))
                        .then(
                            if (index == indexSelected) {
                                Modifier.border(2.dp, Color.Black, (RoundedCornerShape(4.dp)))
                            } else {
                                Modifier
                            }
                        )
                        .padding(vertical = 8.dp),
                )
            }
        }
    }
}