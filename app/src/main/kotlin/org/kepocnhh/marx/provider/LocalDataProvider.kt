package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.util.MutableStorage

internal interface LocalDataProvider {
    val foo: MutableStorage<Foo>
    var bar: List<Bar>
}
