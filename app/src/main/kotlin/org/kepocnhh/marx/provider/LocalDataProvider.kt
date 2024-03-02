package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo

internal interface LocalDataProvider {
    val foo: MutableList<Foo>
    var bar: List<Bar>
}
