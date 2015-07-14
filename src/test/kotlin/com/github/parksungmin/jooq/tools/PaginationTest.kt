package com.github.parksungmin.jooq.tools

import com.github.parksungmin.jooq.tools.database.Tables
import org.h2.Driver
import org.jooq.SQLDialect
import org.jooq.impl.DSL
import org.junit.Assert
import org.junit.Test
import java.io.File

class PaginationTest {
    Test
    fun testPagination() {
        val connection = Driver().connect("jdbc:h2:mem:test-jooq-tools", null)
        try {
            val create = DSL.using(connection, SQLDialect.H2);
            create.execute(File("src/test/resources/db.sql").readText())

            val query = create.selectFrom(Tables.USER).orderBy(Tables.USER.ID)
            val users0 = Pagination.of(query, 0) { User(it.getId(), it.getName()) }
            Assert.assertEquals(1, users0.rows[0].id)
            Assert.assertEquals("Maia", users0.rows[0].name)
            Assert.assertEquals(10, users0.rows[9].id)
            Assert.assertEquals("Julian", users0.rows[9].name)

            val users1 = Pagination.of(query, 1) { User(it.getId(), it.getName()) }
            Assert.assertEquals(11, users1.rows[0].id)
            Assert.assertEquals(20, users1.rows[9].id)
        } finally {
            connection.close()
        }
    }

    data class User(val id: Int, val name: String)
}
