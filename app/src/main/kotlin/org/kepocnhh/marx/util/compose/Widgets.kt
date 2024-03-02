package org.kepocnhh.marx.util.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ColumnButton(
    text: String,
    onClick: () -> Unit,
) {
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .wrapContentHeight(),
        text = text,
        style = TextStyle(
            color = Color.Black,
            fontSize = 17.sp,
            textAlign = TextAlign.Center,
        ),
    )
}

@Composable
internal fun RectButton(
    text: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clickable(onClick = onClick)
            .background(Color.Black),
    ) {
        BasicText(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(),
            text = text,
            style = TextStyle(
                color = Color.White,
                fontSize = 17.sp,
            ),
        )
    }
}

@Composable
internal fun ColumnText(
    text: String,
) {
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .wrapContentHeight(),
        text = text,
        style = TextStyle(
            color = Color.Black,
            fontSize = 17.sp,
        ),
    )
}

@Composable
internal fun ColumnText(
    text: String,
    onClick: () -> Unit,
) {
    BasicText(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clickable(onClick = onClick)
            .wrapContentHeight(),
        text = text,
        style = TextStyle(
            color = Color.Black,
            fontSize = 17.sp,
        ),
    )
}
