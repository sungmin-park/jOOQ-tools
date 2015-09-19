package com.github.parksungmin.jooq.tools

import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.dslContext

public class Pagination<E>(public val page: Int, public val rows: List<E>, public val totalCount: Int, numberOfRowsPerPage: Int, numberOfPagesPerNavigation: Int) {
    public val navigation: Navigation = Navigation(page, totalCount, numberOfRowsPerPage, numberOfPagesPerNavigation)

    companion object {
        @JvmStatic
        @JvmOverloads
        public fun <R : Record, E> of(query: SelectLimitStep<R>, page: Int, numberOfRowsPerPage: Int = 10,
                                      numberOfPagesPerNavigation: Int = 10,
                                      mapper: (record: R) -> E): Pagination<E> {
            val totalCount = query.dslContext().fetchCount(query)
            val rows = query.limit(page * numberOfRowsPerPage, numberOfRowsPerPage).fetch().map(mapper)
            return Pagination(page, rows, totalCount, numberOfRowsPerPage, numberOfPagesPerNavigation)
        }
    }

    public data class Navigation(public val current: Int, totalCount: Int, numberOfRowsPerPage: Int, numberOfPagesPerNavigation: Int) {
        public val first: Int = 0
        // lastPage number inclusive
        public val last: Int = Math.max(((totalCount / numberOfRowsPerPage) + Math.min(1, totalCount % numberOfRowsPerPage)) - 1, 0)
        val next: Int = Math.min(current + 1, last)
        public val previous: Int = Math.max((current - 1), 0)
        public val pages: List<Int> = {
            val firstOnPages = (current - (current % numberOfPagesPerNavigation))
            val lastOnPages = Math.min(firstOnPages + numberOfPagesPerNavigation - 1, last)
            (firstOnPages..lastOnPages).toList()
        }()
        public val previousNavigation: Int = Math.max(pages[0] - 1, first)
        public val nextNavigation: Int = Math.min(pages.last() + 1, last)
    }
}