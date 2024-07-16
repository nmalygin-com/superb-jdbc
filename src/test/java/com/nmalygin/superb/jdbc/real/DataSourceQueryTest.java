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

import com.nmalygin.superb.jdbc.H2DataSource;
import com.nmalygin.superb.jdbc.api.Queries;
import com.nmalygin.superb.jdbc.real.handlers.StringListHandler;
import com.nmalygin.superb.jdbc.real.params.StringParam;
import com.nmalygin.superb.jdbc.testdb.CarsDB;
import com.nmalygin.superb.jdbc.testdb.CarsTable;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataSourceQueryTest {

    @Test
    void simpleQuery() throws SQLException {
        DataSource dataSource = new H2DataSource();
        new CarsDB(dataSource).init();
        CarsTable carsTable = new CarsTable(dataSource);
        carsTable.insert(UUID.randomUUID(), "Toyota");
        carsTable.insert(UUID.randomUUID(), "Ford");

        Queries queries = new Dbms(dataSource);
        List<String> names = queries
                .query("SELECT name FROM cars")
                .execute(new StringListHandler("name"));

        assertEquals(2, names.size());
        assertTrue(names.contains("Toyota"));
        assertTrue(names.contains("Ford"));
    }

    @Test
    void withParamsQuery() throws SQLException {
        DataSource dataSource = new H2DataSource();
        new CarsDB(dataSource).init();
        CarsTable carsTable = new CarsTable(dataSource);
        carsTable.insert(UUID.randomUUID(), "Toyota");
        carsTable.insert(UUID.randomUUID(), "Ford");

        Queries queries = new Dbms(dataSource);
        List<String> names = queries
                .query("SELECT name FROM cars WHERE name = ?",
                        new StringParam("Toyota"))
                .execute(new StringListHandler("name"));

        assertEquals(1, names.size());
        assertTrue(names.contains("Toyota"));
    }

    @Test
    void buildQuery() throws SQLException {
        DataSource dataSource = new H2DataSource();
        new CarsDB(dataSource).init();
        CarsTable carsTable = new CarsTable(dataSource);
        carsTable.insert(UUID.randomUUID(), "Toyota");
        carsTable.insert(UUID.randomUUID(), "Ford");

        Queries queries = new Dbms(dataSource);
        List<String> names = queries
                .query("SELECT name FROM cars ")
                .append("ORDER BY name")
                .execute(new StringListHandler("name"));

        assertEquals(2, names.size());
        assertEquals("Ford", names.get(0));
    }
}