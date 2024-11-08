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

import com.nmalygin.superb.jdbc.api.Batch;
import com.nmalygin.superb.jdbc.api.Argument;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

final class ClosingConnectionBatch implements Batch {

    private final PreparedStatement preparedStatement;

    ClosingConnectionBatch(final PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    @Override
    public void put(final Argument... arguments) throws SQLException {
        int index = 1;
        for (final Argument argument : arguments) {
            argument.pass(preparedStatement, index++);
        }
        preparedStatement.addBatch();
    }

    @Override
    public void apply() throws SQLException {
        preparedStatement.executeBatch();
    }

    @Override
    public void close() throws SQLException {
        try (Connection ignored = preparedStatement.getConnection()) {
            preparedStatement.close();
        }
    }
}
