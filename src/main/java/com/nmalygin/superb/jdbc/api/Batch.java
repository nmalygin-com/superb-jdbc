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
 * An object representing a batch of operations.
 *
 * @author Nikolai Malygin
 */
public interface Batch extends AutoCloseable {

    /**
     * Adding an operation to the batch.
     *
     * @param arguments Arguments of one operation
     * @throws SQLException if parameterIndex does not correspond to a parameter marker in the SQL statement;
     * if a database access error occurs or this method is called on a closed PreparedStatement
     */
    void put(Argument... arguments) throws SQLException;

    /**
     * Apply the batch to the database.
     *
     * @throws SQLException if a database access error occurs, this method is called on a closed Statement or the driver does not support batch statements.
     * if one of the commands sent to the database fails to execute properly or attempts to return a result set.
     * if the driver has determined that the timeout value that was specified by the setQueryTimeout method has been exceeded and has at least attempted to cancel the currently running Statement
     */
    void apply() throws SQLException;

    /**
     * Releases the batch object and JDBC resources. It is generally good practice to release resources as
     * soon as you are finished with them to avoid tying up database resources.
     *
     * @throws SQLException if a database access error occurs
     */
    @Override
    void close() throws SQLException;
}
