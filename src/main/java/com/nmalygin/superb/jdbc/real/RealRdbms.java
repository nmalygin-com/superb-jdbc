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

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class RealRdbms implements Rdbms {

    private final DataSource dataSource;

    public RealRdbms(final DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Query query(final String sql, final Param... withParams) {
        return new DataSourceQuery(dataSource, sql, withParams);
    }

    @Override
    public Change change(final String sql, final Param... withParams) {
        return new DataSourceChange(dataSource, sql, withParams);
    }

    @Override
    public Batch batch(final String sql) throws SQLException {
        final Connection connection = dataSource.getConnection();
        try {
            return new ClosingConnectionBatch(connection.prepareStatement(sql));
        } catch (Throwable t) {
            connection.close();
            throw t;
        }
    }

    @Override
    public Transaction transaction() throws SQLException {
        final Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false);
        } catch (Throwable t) {
            connection.close();
            throw t;
        }

        return new ConnectionTransaction(connection);
    }

    @Override
    public Transaction transaction(final int isolationLevel) throws SQLException {
        final Connection connection = dataSource.getConnection();

        try {
            connection.setAutoCommit(false);
        } catch (Throwable t) {
            connection.close();
            throw t;
        }

        try {
            connection.setTransactionIsolation(isolationLevel);
        } catch (Throwable t) {
            connection.close();
            throw t;
        }

        return new ConnectionTransaction(connection);
    }
}
