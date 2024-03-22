package org.kepocnhh.marx.module.app

import org.kepocnhh.marx.provider.Contexts
import org.kepocnhh.marx.provider.Locals
import org.kepocnhh.marx.provider.Loggers
import org.kepocnhh.marx.provider.Remotes
import org.kepocnhh.marx.provider.Serializer

internal data class Injection(
    val contexts: Contexts,
    val loggers: Loggers,
    val locals: Locals,
    val remotes: Remotes,
    val serializer: Serializer,
)
