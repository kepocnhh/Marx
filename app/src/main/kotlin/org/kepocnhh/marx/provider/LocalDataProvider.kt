package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Bar
import org.kepocnhh.marx.entity.Foo

internal interface LocalDataProvider {
    var foo: List<Foo>
    var bar: List<Bar>
}
