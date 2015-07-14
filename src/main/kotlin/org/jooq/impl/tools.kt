package org.jooq.impl

import org.jooq.DSLContext
import org.jooq.Select

fun Select<*>.dslContext(): DSLContext {
    val configuration = ((this as AbstractDelegatingQuery<*>).getDelegate() as AbstractQuery).configuration()
    return DSL.using(configuration)
}

