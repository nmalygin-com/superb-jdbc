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

import com.nmalygin.superb.jdbc.api.Change;
import com.nmalygin.superb.jdbc.api.Param;
import com.nmalygin.superb.jdbc.api.Query;
import com.nmalygin.superb.jdbc.api.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.HashMap;
import java.util.Map;

final class ConnectionTransaction implements Transaction {

    private final Connection connection;
    private final Map<String, Savepoint> savePoints = new HashMap<>();

    ConnectionTransaction(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Query query(String sql, Param... withParams) {
        return new ConnectionQuery(connection, sql, withParams);
    }

    @Override
    public Change change(String sql, Param... withParams) {
        return new ConnectionChange(connection, sql, withParams);
    }

    @Override
    public void commit() throws SQLException {
        connection.commit();
    }

    @Override
    public void setSavepoint(String withName) throws SQLException {
        if (savePoints.containsKey(withName)) {
            throw new IllegalArgumentException("Savepoint with name: " + withName + " already exists.");
        }

        savePoints.put(withName, connection.setSavepoint(withName));
    }

    @Override
    public void rollback() throws SQLException {
        connection.rollback();
    }

    @Override
    public void rollbackTo(String savepointWithName) throws SQLException {
        if (!savePoints.containsKey(savepointWithName)) {
            throw new IllegalArgumentException("Savepoint with name: " + savepointWithName + " not found.");
        }

        connection.rollback(savePoints.get(savepointWithName));
    }

    @Override
    public void close() {
        savePoints.clear();
        // todo: to think about implementation
        if (connection != null) {
            try {
                connection.close();
            } catch (Throwable ignore) {
                // todo: add logging
            }
        }
    }
}
