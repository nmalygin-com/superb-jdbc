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

import com.nmalygin.superb.jdbc.api.*;
import com.nmalygin.superb.jdbc.api.arguments.IntArgument;
import com.nmalygin.superb.jdbc.api.arguments.ObjectArgument;
import com.nmalygin.superb.jdbc.api.arguments.StringArgument;
import com.nmalygin.superb.jdbc.api.handlers.ColumnToListRsh;
import com.nmalygin.superb.jdbc.api.handlers.columns.StringColumn;
import com.nmalygin.superb.jdbc.real.testdb.BooksTable;
import com.nmalygin.superb.jdbc.real.testdb.DataSourceBooksTable;
import com.nmalygin.superb.jdbc.real.testdb.H2DataSource;
import com.nmalygin.superb.jdbc.real.testdb.LibraryDB;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReadmeTest {

    @Test
    void quickStart() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Rdbms rdbms = new RealRdbms(dataSource);
        rdbms.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();

        assertEquals(1, booksTable.books().size());
    }

    @Test
    void querySimpleExample() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);
        booksTable.insert(UUID.randomUUID(), "Clean Code");

        Queries queries = new RealRdbms(dataSource);
        List<String> titles = queries
                .query("SELECT title FROM books")
                .executeWith(new ColumnToListRsh<>(new StringColumn("title")));

        assertEquals(1, titles.size());
        assertEquals("Clean Code", titles.get(0));
    }

    @Test
    void queryUsingParams() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);
        booksTable.insert(UUID.randomUUID(), "Clean Code");
        booksTable.insert(UUID.randomUUID(), "Code Complete");

        Queries queries = new RealRdbms(dataSource);
        List<String> titles = queries
                .query(
                        "SELECT title FROM books WHERE title = ? OR title = ?",
                        new StringArgument("Clean Code"),
                        new StringArgument("Code Complete")
                )
                .executeWith(new ColumnToListRsh<>(new StringColumn("title")));

        assertEquals(2, titles.size());
    }

    @Test
    void queryBuilding() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);
        booksTable.insert(UUID.randomUUID(), "Clean Code");

        Queries queries = new RealRdbms(dataSource);

        List<String> titles = queries.query("SELECT title FROM books ")
                .append("WHERE title LIKE ? ", new StringArgument("Clean%"))
                .append("LIMIT ?", new IntArgument(10))
                .executeWith(new ColumnToListRsh<>(new StringColumn("title")));

        assertEquals(1, titles.size());
        assertEquals("Clean Code", titles.get(0));
    }

    @Test
    void queryCustomRsh() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);
        booksTable.insert(UUID.randomUUID(), "Clean Code");

        class Book {
            private final UUID id;
            private final Queries queries;

            public Book(ResultSet resultSet, Queries queries) throws SQLException {
                this.id = resultSet.getObject("id", UUID.class);
                this.queries = queries;
            }

            // methods: title, changeTitle, etc.
        }

        Queries queries = new RealRdbms(dataSource);
        List<Book> titles = queries
                .query("SELECT id FROM books")
                .executeWith(resultSet -> {
                            final List<Book> books = new ArrayList<>();
                            while (resultSet.next()) {
                                books.add(new Book(resultSet, queries));
                            }

                            return books;
                        }
                );

        assertEquals(1, titles.size());
    }

    @Test
    void changeSimpleExample() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Changes changes = new RealRdbms(dataSource);

        changes
                .change("INSERT INTO books(title) VALUES ('Clean Code')")
                .apply();

        assertEquals(1, booksTable.books().size());
    }

    @Test
    void changeWithParams() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Changes changes = new RealRdbms(dataSource);

        changes
                .change("INSERT INTO books(title) VALUES (?)", new StringArgument("Clean Code"))
                .apply();

        assertEquals(1, booksTable.books().size());
    }

    @Test
    void batchSimpleExample() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Batches batches = new RealRdbms(dataSource);

        try (Batch batch = batches.batch("INSERT INTO books(title) VALUES (?)")) {
            batch.put(new StringArgument("Clean Code"));
            batch.put(new StringArgument("Code Complete"));
            batch.put(new StringArgument("Effective Java"));
            batch.apply();
        }

        assertEquals(3, booksTable.books().size());
    }

    @Test
    void transactionSimpleExample() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Transactions transactions = new RealRdbms(dataSource);

        try (Transaction transaction = transactions.transaction()) {
            transaction.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();
            transaction.change("INSERT INTO books(title) VALUES ('Code Complete')").apply();
            transaction.change("INSERT INTO books(title) VALUES ('Effective Java')").apply();
            transaction.commit();
        }

        assertEquals(3, booksTable.books().size());
    }

    @Test
    void transactionUsingSavepoints() throws SQLException {
        final DataSource dataSource = new H2DataSource();
        new LibraryDB(dataSource).init();
        final BooksTable booksTable = new DataSourceBooksTable(dataSource);

        Transactions transactions = new RealRdbms(dataSource);

        try (Transaction transaction = transactions.transaction()) {
            transaction.change("INSERT INTO books(title) VALUES ('Clean Code')").apply();
            transaction.setSavepoint("MySavepoint");
            transaction.change("INSERT INTO books(title) VALUES ('Code Complete')").apply();
            transaction.rollbackTo("MySavepoint");
            transaction.change("INSERT INTO books(title) VALUES ('Effective Java')").apply();
            transaction.commit();
        }

        assertEquals(2, booksTable.books().size());
    }
}
