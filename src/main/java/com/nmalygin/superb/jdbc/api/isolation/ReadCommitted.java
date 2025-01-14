/*
 * MIT License
 *
 * Copyright (c) 2025 Nikolai Malygin
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

package com.nmalygin.superb.jdbc.api.isolation;

import com.nmalygin.superb.jdbc.api.IsolationLevel;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * The read committed isolation level
 *
 * @author Nikolai Malygin
 */
@SuppressWarnings("PMD.AtLeastOneConstructor")
public final class ReadCommitted implements IsolationLevel {

    /**
     * Apply the isolation level for the connection.
     *
     * @param connection JDBC Connection
     * @throws SQLException SQLException
     */
    @Override
    public void applyTo(final Connection connection) throws SQLException {
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    }
}
