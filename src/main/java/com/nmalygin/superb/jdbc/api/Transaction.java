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

package com.nmalygin.superb.jdbc.api;

import java.sql.SQLException;

/**
 * The transaction object.
 *
 * @author Nikolai Malygin
 */
public interface Transaction extends Queries, Changes, Batches, AutoCloseable {
    /**
     *
     * @throws SQLException SQLException
     */
    void commit() throws SQLException;

    /**
     *
     * @param name savepoint name
     * @throws SQLException SQLException
     */
    void setSavepoint(String name) throws SQLException;

    /**
     *
     * @throws SQLException SQLException
     */
    void rollback() throws SQLException;

    /**
     *
     * @param savepoint savepoint name
     * @throws SQLException SQLException
     */
    void rollbackTo(String savepoint) throws SQLException;

    /**
     *
     * @throws SQLException SQLException
     */
    @Override
    void close() throws SQLException;
}
