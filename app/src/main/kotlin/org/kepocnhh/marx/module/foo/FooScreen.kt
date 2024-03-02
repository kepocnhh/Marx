package org.kepocnhh.marx.module.foo

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import org.kepocnhh.marx.App
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.util.compose.BackHandler
import org.kepocnhh.marx.util.compose.ColumnButton
import org.kepocnhh.marx.util.compose.ColumnText
import java.util.UUID

@Composable
internal fun FooScreen(
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
        val listState = remember { mutableStateOf<List<Foo>?>(null) }
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
                                App.ldp.foo = App.ldp.foo.toMutableList().also { list ->
                                    list.removeIf { it.id == deleteId }
                                }
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
                                if (text.isNotBlank()) {
                                    App.ldp.foo = App.ldp.foo.toMutableList().also { list ->
                                        val item = Foo(
                                            id = UUID.randomUUID(),
                                            text = valueState.value,
                                        )
                                        list.add(item)
                                    }
                                    listState.value = null
                                    addState.value = false
                                }
                            },
                        )
                    }
                }
            }
        }
        LaunchedEffect(listState.value) {
            if (listState.value == null) {
                listState.value = App.ldp.foo
            }
        }
        val list = listState.value.orEmpty()
        LazyColumn(
            contentPadding = PaddingValues(vertical = 64.dp),
        ) {
            items(
                count = list.size,
                key = { list[it].id },
            ) { index ->
                val item = list[index]
                ColumnText(
                    text = "$index) text: \"${item.text}\"",
                    onClick = {
                        deleteState.value = item.id
                    },
                )
            }
        }
        Box(
            modifier = Modifier
                .padding(end = 32.dp, bottom = 64.dp)
                .size(64.dp)
                .clickable {
                    addState.value = true
                }
                .background(Color.Black)
                .align(Alignment.BottomEnd),
        ) {
            BasicText(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
                text = "+",
                style = TextStyle(
                    color = Color.White,
                    fontSize = 17.sp,
                ),
            )
        }
    }
}
