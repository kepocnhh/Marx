package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta

internal interface Locals {
    var metas: List<Meta>
    var foo: List<Foo>
}
