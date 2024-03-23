package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta
import java.util.UUID

internal interface Locals {
    var metas: List<Meta>
    var foo: List<Foo>

    fun getByMetaId(id: UUID): ByteArray
}
