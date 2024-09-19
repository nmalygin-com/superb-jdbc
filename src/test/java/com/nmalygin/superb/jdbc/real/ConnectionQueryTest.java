/*
 * MIT License
 *
 * Copyright (c) 2024 Nikolai Malygin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.nmalygin.superb.jdbc.real;

import com.nmalygin.superb.jdbc.api.handlers.columns.StringColumn;
import com.nmalygin.superb.jdbc.real.testdb.H2DataSource;
import com.nmalygin.superb.jdbc.api.handlers.ColumnToListRsh;
import com.nmalygin.superb.jdbc.real.testdb.LibraryDB;
import com.nmalygin.superb.jdbc.real.testdb.BooksTable;
import com.nmalygin.superb.jdbc.real.testdb.DataSourceBooksTable;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionQueryTest {
    @Test
    void simpleInsert() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);
        final String title = "Clean Code";

        booksTable.insert(UUID.randomUUID(), title);

        try (Connection connection = dataSource.getConnection()) {
            final List<String> titles = new ConnectionQuery(connection, "SELECT title FROM books")
                    .executeWith(new ColumnToListRsh<>(new StringColumn("title")));

            assertEquals(1, titles.size());
            assertTrue(titles.contains(title));
        }
    }
}