package com.github.parksungmin.jooq.tools

import com.github.parksungmin.jooq.tools.database.Tables
import org.h2.Driver
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.Assert
import org.junit.Test
import java.io.File

class PaginationTest {
    fun db(run: (create: DSLContext) -> Unit) {
        val connection = Driver().connect("jdbc:h2:mem:test-jooq-tools", null)
        try {
            val create = DSL.using(connection, SQLDialect.H2);
            create.execute(File("src/test/resources/db.sql").readText())
            run(create)
        } finally {
            connection.close()
        }
    }

    @Test
    fun testPaginationOf() {
        db { create ->
            val pages = (0..9).toList()
            val users0 = Pagination.of(create.selectFrom(Tables.USER).orderBy(Tables.USER.ID), 0) { User(it.id, it.name) }
            Assert.assertEquals(123, users0.totalCount)
            Assert.assertEquals(1, users0.rows[0].id)
            Assert.assertEquals("Maia", users0.rows[0].name)
            Assert.assertEquals(10, users0.rows[9].id)
            Assert.assertEquals("Julian", users0.rows[9].name)
            Assert.assertEquals(0, users0.navigation.current)
            Assert.assertEquals(0, users0.navigation.first)
            Assert.assertEquals(0, users0.navigation.previousNavigation)
            Assert.assertEquals(0, users0.navigation.previous)
            Assert.assertEquals(pages, users0.navigation.pages)
            Assert.assertEquals(1, users0.navigation.next)
            Assert.assertEquals(10, users0.navigation.nextNavigation)
            Assert.assertEquals(12, users0.navigation.last)

            val users1 = Pagination.of(create.selectFrom(Tables.USER).orderBy(Tables.USER.ID), 1) { User(it.id, it.name) }
            Assert.assertEquals(11, users1.rows[0].id)
            Assert.assertEquals(20, users1.rows[9].id)
            Assert.assertEquals(1, users1.navigation.current)
            Assert.assertEquals(0, users1.navigation.first)
            Assert.assertEquals(0, users1.navigation.previousNavigation)
            Assert.assertEquals(0, users1.navigation.previous)
            Assert.assertEquals(pages, users1.navigation.pages)
            Assert.assertEquals(2, users1.navigation.next)
            Assert.assertEquals(10, users1.navigation.nextNavigation)
            Assert.assertEquals(12, users1.navigation.last)

            val users2 = Pagination.of(create.selectFrom(Tables.USER).orderBy(Tables.USER.ID), 2) { User(it.id, it.name) }
            Assert.assertEquals(2, users2.navigation.current)
            Assert.assertEquals(0, users2.navigation.first)
            Assert.assertEquals(0, users2.navigation.previousNavigation)
            Assert.assertEquals(1, users2.navigation.previous)
            Assert.assertEquals(pages, users2.navigation.pages)
            Assert.assertEquals(3, users2.navigation.next)
            Assert.assertEquals(10, users2.navigation.nextNavigation)
            Assert.assertEquals(12, users2.navigation.last)

            val users10 = Pagination.of(create.selectFrom(Tables.USER).orderBy(Tables.USER.ID), 10) { User(it.id, it.name) }
            Assert.assertEquals(10, users10.navigation.current)
            Assert.assertEquals(0, users10.navigation.first)
            Assert.assertEquals(9, users10.navigation.previousNavigation)
            Assert.assertEquals(9, users10.navigation.previous)
            Assert.assertEquals((10..12).toList(), users10.navigation.pages)
            Assert.assertEquals(11, users10.navigation.next)
            Assert.assertEquals(12, users10.navigation.nextNavigation)
            Assert.assertEquals(12, users10.navigation.last)

            val users12 = Pagination.of(create.selectFrom(Tables.USER).orderBy(Tables.USER.ID), 12) { User(it.id, it.name) }
            Assert.assertEquals(12, users12.navigation.current)
            Assert.assertEquals(0, users12.navigation.first)
            Assert.assertEquals(9, users12.navigation.previousNavigation)
            Assert.assertEquals(11, users12.navigation.previous)
            Assert.assertEquals((10..12).toList(), users12.navigation.pages)
            Assert.assertEquals(12, users12.navigation.next)
            Assert.assertEquals(12, users12.navigation.nextNavigation)
            Assert.assertEquals(12, users12.navigation.last)
        }
    }

    @Test
    fun testEmptyPaginationOf() {
        db { create ->
            val users = Pagination.of(create.selectFrom(Tables.USER).where(Tables.USER.ID.lt(0)), 0) { User(it.id, it.name) }
            Assert.assertEquals(0, users.navigation.current)
            Assert.assertEquals(0, users.navigation.first)
            Assert.assertEquals(0, users.navigation.previousNavigation)
            Assert.assertEquals(0, users.navigation.previous)
            Assert.assertEquals((0..0).toList(), users.navigation.pages)
            Assert.assertEquals(0, users.navigation.next)
            Assert.assertEquals(0, users.navigation.nextNavigation)
            Assert.assertEquals(0, users.navigation.last)
        }
    }

    @Test
    fun testNumberOfRowsPerPage() {
        db { create ->
            val users = Pagination.of(create.selectFrom(Tables.USER).where(Tables.USER.ID.lt(0)), 0, numberOfRowsPerPage = 20) {
                User(it.id, it.name)
            }
            Assert.assertEquals(0, users.rows.size)
        }
    }

    data class User(val id: Int, val name: String)
}
