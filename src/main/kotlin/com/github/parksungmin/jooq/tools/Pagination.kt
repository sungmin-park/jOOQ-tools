package com.github.parksungmin.jooq.tools

import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.dslContext
import kotlin.platform.platformStatic

public class Pagination<E>(public val rows: List<E>, public val totalCount: Int) {
    companion object {
        platformStatic
        jvmOverloads
        public fun <R : Record, E> of(query: SelectLimitStep<R>, page: Int, numberOfRowsPerPage: Int = 10,
                                      mapper: (record: R) -> E): Pagination<E> {
            val totalCount = query.dslContext().fetchCount(query)
            val rows = query.limit(page * numberOfRowsPerPage, numberOfRowsPerPage).fetch().map(mapper)
            return Pagination(rows, totalCount)
        }
    }
}