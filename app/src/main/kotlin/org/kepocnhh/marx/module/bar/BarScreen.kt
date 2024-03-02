package org.kepocnhh.marx.module.bar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kepocnhh.marx.App
import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.util.compose.BackHandler
import org.kepocnhh.marx.util.compose.ColumnButton
import org.kepocnhh.marx.util.compose.ColumnText
import org.kepocnhh.marx.util.compose.RectButton
import java.util.Date
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun BarScreen(
    onBack: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        BackHandler(block = onBack)
        val addState = remember { mutableStateOf(false) }
        val deleteState = remember { mutableStateOf<UUID?>(null) }
        val listState = remember { mutableStateOf<List<Bar>?>(null) }
        val deleteId = deleteState.value
        if (deleteId != null) {
            Dialog(onDismissRequest = { deleteState.value = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                ) {
                    Column {
                        ColumnText(
                            text = "Delete item $deleteId?",
                        )
                        ColumnButton(
                            text = "Yes",
                            onClick = {
                                App.ldp.bar.removeIf { it.id == deleteId }
                                listState.value = null
                                deleteState.value = null
                            },
                        )
                    }
                }
            }
        }
        if (addState.value) {
            Dialog(onDismissRequest = { addState.value = false }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                ) {
                    val valueState = remember { mutableStateOf("") }
                    Column {
                        BasicTextField(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .wrapContentHeight(),
                            value = valueState.value,
                            onValueChange = {
                                valueState.value = it
                            },
                        )
                        ColumnButton(
                            text = "Add",
                            onClick = {
                                val text = valueState.value
                                val year = try {
                                    text.slice(0..3).toInt()
                                } catch (_: Throwable) {
                                    null
                                }
                                val month = try {
                                    text.slice(4..5).toInt()
                                } catch (_: Throwable) {
                                    null
                                }
                                val day = try {
                                    text.slice(6..7).toInt()
                                } catch (_: Throwable) {
                                    null
                                }
                                if (year != null && year > 0) {
                                    if (month != null && month > 0) {
                                        if (day != null && day > 0) {
                                            val item = Bar(
                                                id = UUID.randomUUID(),
                                                date = Date(year - 1900, month - 1, day).time.milliseconds,
                                            )
                                            App.ldp.bar.add(item)
                                            listState.value = null
                                            addState.value = false
                                        }
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
        LaunchedEffect(listState.value) {
            if (listState.value == null) {
                listState.value = App.ldp.bar
            }
        }
        val list = listState.value
        if (list.isNullOrEmpty()) {
            BasicText(
                modifier = Modifier
                    .align(Alignment.Center),
                text = "no items",
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 17.sp,
                ),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 64.dp),
            ) {
                list.forEachIndexed { index, item ->
                    item(
                        key = item.id,
                    ) {
                        ColumnText(
                            text = "$index) date: \"${Date(item.date.inWholeMilliseconds)}\"",
                            onClick = {
                                deleteState.value = item.id
                            },
                        )
                    }
                }
            }
        }
        Box(
            modifier = Modifier
                .padding(end = 32.dp, bottom = 64.dp)
                .align(Alignment.BottomEnd),
        ) {
            RectButton(
                text = "+",
                onClick = {
                    addState.value = true
                },
            )
        }
    }
}
