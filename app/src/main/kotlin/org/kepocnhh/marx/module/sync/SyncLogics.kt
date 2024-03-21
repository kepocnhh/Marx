package org.kepocnhh.marx.module.sync

import org.kepocnhh.marx.module.app.Injection
import sp.kx.logics.Logics

internal class SyncLogics(
    private val injection: Injection,
) : Logics(injection.contexts.main) {
    fun itemsSync() = launch {

    }
}
