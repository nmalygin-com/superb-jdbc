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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

final class ConnectionTransaction implements Transaction {

    private final Connection connection;
    private final Map<String, Savepoint> savePoints;

    ConnectionTransaction(final Connection connection, final Map<String, Savepoint> savePoints) {
        this.connection = connection;
        this.savePoints = savePoints;
    }

    ConnectionTransaction(final Connection connection) {
        this(connection, new HashMap<>());
    }

    @Override
    public Query query(final String sql, final Argument... withArguments) {
        return new ConnectionQuery(connection, sql, withArguments);
    }

    @Override
    public Change change(final String sql, final Argument... withArguments) {
        return new ConnectionChange(connection, sql, withArguments);
    }

    @Override
    public Batch batch(final String sql) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);

        return new ClosingPreparedStatementBatch(preparedStatement);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void setSavepoint(final String name) throws SQLException {
        if (savePoints.containsKey(name)) {
            throw new IllegalArgumentException("Savepoint with name: " + name + " already exists.");
        }

        savePoints.put(name, connection.setSavepoint(name));
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void rollbackTo(final String savepoint) throws SQLException {
        if (!savePoints.containsKey(savepoint)) {
            throw new IllegalArgumentException("Savepoint with name: " + savepoint + " not found.");
        }

        connection.rollback(savePoints.get(savepoint));
    }

    @Override
    public void close() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
