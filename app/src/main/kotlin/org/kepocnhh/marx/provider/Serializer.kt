package org.kepocnhh.marx.provider

import org.kepocnhh.marx.entity.Foo
import org.kepocnhh.marx.entity.Meta

internal interface Serializer {
    val meta: Transformer<Meta>
    val foo: ListTransformer<Foo>
}
