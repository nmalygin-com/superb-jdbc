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

import com.nmalygin.superb.jdbc.real.testdb.H2DataSource;
import com.nmalygin.superb.jdbc.real.testdb.Book;
import com.nmalygin.superb.jdbc.real.testdb.LibraryDB;
import com.nmalygin.superb.jdbc.real.testdb.DataSourceBooksTable;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionChangeTest {
    @Test
    void simpleInsert() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final UUID id = UUID.randomUUID();
        final String title = "Clean Code";

        try (Connection connection = dataSource.getConnection()) {
            new ConnectionChange(connection, "INSERT INTO books (id, title) VALUES ('" + id + "', '" + title + "')")
                    .apply();
        }

        final List<Book> books = new DataSourceBooksTable(dataSource).books();
        assertEquals(1, books.size());
        assertEquals(id, books.get(0).id());
        assertEquals(title, books.get(0).title());
    }
}