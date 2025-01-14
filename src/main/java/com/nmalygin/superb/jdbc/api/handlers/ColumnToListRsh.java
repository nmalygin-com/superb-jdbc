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

package com.nmalygin.superb.jdbc.api.handlers;

import com.nmalygin.superb.jdbc.api.ResultSetHandler;
import com.nmalygin.superb.jdbc.api.handlers.columns.Column;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * ColumnToListRsh
 *
 * @author Nikolai Malygin
 */
public final class ColumnToListRsh<T> implements ResultSetHandler<List<T>> {

    private final Column<T> column;

    /**
     *
     * @param column typed column
     */
    public ColumnToListRsh(final Column<T> column) {
        this.column = column;
    }

    /**
     *
     * @param resultSet ResultSet
     * @return list values of the column
     * @throws SQLException SQLException
     */
    @Override
    public List<T> handle(final ResultSet resultSet) throws SQLException {
        final List<T> list = new ArrayList<>();
        while (resultSet.next()) {
            list.add(column.cellValue(resultSet));
        }

        return list;
    }
}
