package com.github.parksungmin.jooq.tools

import org.jooq.Record
import org.jooq.SelectLimitStep
import org.jooq.impl.dslContext

class Pagination<E>(val page: Int, val rows: List<E>, val totalCount: Int, numberOfRowsPerPage: Int, numberOfPagesPerNavigation: Int) {
    val navigation: Navigation = Navigation(page, totalCount, numberOfRowsPerPage, numberOfPagesPerNavigation)

    companion object {
        @JvmStatic
        @JvmOverloads
        fun <R : Record, E> of(query: SelectLimitStep<R>, page: Int, numberOfRowsPerPage: Int = 10,
                               numberOfPagesPerNavigation: Int = 10,
                               mapper: (record: R) -> E): Pagination<E> {
            val totalCount = query.dslContext().fetchCount(query)
            val rows = query.limit(page * numberOfRowsPerPage, numberOfRowsPerPage).fetch().map(mapper)
            return Pagination(page, rows, totalCount, numberOfRowsPerPage, numberOfPagesPerNavigation)
        }
    }

    data class Navigation(val current: Int, val totalCount: Int, val numberOfRowsPerPage: Int, val numberOfPagesPerNavigation: Int) {
        val first: Int = 0
        // lastPage number inclusive
        val last: Int = Math.max(((totalCount / numberOfRowsPerPage) + Math.min(1, totalCount % numberOfRowsPerPage)) - 1, 0)
        val next: Int = Math.min(current + 1, last)
        val previous: Int = Math.max((current - 1), 0)
        val pages: List<Int> = {
            val firstOnPages = (current - (current % numberOfPagesPerNavigation))
            val lastOnPages = Math.min(firstOnPages + numberOfPagesPerNavigation - 1, last)
            (firstOnPages..lastOnPages).toList()
        }()
        val previousNavigation: Int = Math.max(pages[0] - 1, first)
        val nextNavigation: Int = Math.min(pages.last() + 1, last)
    }
}