package com.github.parksungmin.jooq.tools

import org.jooq.Record
import org.jooq.RecordMapper
import org.jooq.SelectLimitStep
import org.jooq.impl.dslContext

public class Pagination<E>(public val rows: List<E>, public val totalCount: Int) {
    companion object {
        public fun <R : Record, E> of(query: SelectLimitStep<R>, page: Int, mapper: (record: R) -> E): Pagination<E> {
            val totalCount = query.dslContext().fetchCount(query)
            val rows = query.limit(0, 10).fetch().map(mapper)
            return Pagination(rows, totalCount)
        }
    }
}