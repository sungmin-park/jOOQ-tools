package org.jooq.impl

import org.jooq.DSLContext
import org.jooq.Select

object tools {
    fun dslContext(select: Select<*>): DSLContext {
        val configuration = ((select as AbstractDelegatingQuery<*>).delegate as AbstractQuery).configuration()
        return DSL.using(configuration)
    }
}

fun Select<*>.dslContext(): DSLContext = tools.dslContext(this)
