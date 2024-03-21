package org.kepocnhh.marx.module.foo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.collect
import org.kepocnhh.marx.App
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import org.kepocnhh.marx.module.sync.SyncLogics
import org.kepocnhh.marx.util.compose.BackHandler
import org.kepocnhh.marx.util.compose.ColumnButton
import org.kepocnhh.marx.util.compose.ColumnText
import org.kepocnhh.marx.util.compose.RectButton
import java.util.Date
import java.util.UUID
import kotlin.time.Duration.Companion.milliseconds

@Composable
private fun FooScreen(
    meta: Meta,
    items: List<Foo>,
) {
    val logics = App.logics<FooLogics>()
    val syncLogics = App.logics<SyncLogics>()
    val syncState = syncLogics.state.collectAsState().value
    LaunchedEffect(Unit) {
        syncLogics.broadcast.collect { broadcast ->
            when (broadcast) {
                is SyncLogics.Broadcast.OnError -> {
                    // todo
                }
                is SyncLogics.Broadcast.OnSuccess -> {
                    // todo
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        val addState = remember { mutableStateOf(false) }
        val deleteState = remember { mutableStateOf<UUID?>(null) }
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
                                logics.deleteItem(id = deleteId)
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
                                    logics.addItem(text)
                                    addState.value = false
                                }
                            },
                        )
                    }
                }
            }
        }
        if (items.isEmpty()) {
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
                items.forEachIndexed { index, item ->
                    item(
                        key = item.id,
                    ) {
                        ColumnText(
                            text = "$index) id: ${item.id}\ntext: \"${item.text}\"\ncreated: \"${Date(item.created.inWholeMilliseconds)}\"",
                            onClick = {
                                deleteState.value = item.id
                            },
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 32.dp,
                    vertical = 64.dp,
                )
                .align(Alignment.BottomCenter),
        ) {
            RectButton(
                text = "sync",
                onClick = {
                    syncLogics.itemsSync(meta)
                },
            )
            Spacer(modifier = Modifier.weight(1f))
            RectButton(
                text = "+",
                onClick = {
                    addState.value = true
                },
            )
        }
        if (syncState.loading) {
            BasicText(
                modifier = Modifier.align(Alignment.Center),
                text = "loading...",
            )
        }
    }
}

@Composable
internal fun FooScreen(
    onBack: () -> Unit,
) {
    BackHandler(block = onBack)
    val logics = App.logics<FooLogics>()
    val state = logics.state.collectAsState().value
    LaunchedEffect(Unit) {
        if (state == null) {
            logics.requestItems()
        }
    }
    if (state != null) {
        FooScreen(
            meta = state.meta,
            items = state.items,
        )
    }
}
