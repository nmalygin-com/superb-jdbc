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

import com.nmalygin.superb.jdbc.api.Param;
import com.nmalygin.superb.jdbc.api.Query;
import com.nmalygin.superb.jdbc.api.ResultSetHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

final class DataSourceQuery implements Query {

    private final DataSource dataSource;
    private final Sql sql;

    DataSourceQuery(DataSource dataSource, Sql sql) {
        this.dataSource = dataSource;
        this.sql = sql;
    }

    DataSourceQuery(DataSource dataSource, String sqlFragment, Param... withParams) {
        this(dataSource, new NotThreadSafeSql(sqlFragment, withParams));
    }

    @Override
    public Query append(String sqlFragment, Param... withParams) {
        sql.append(sqlFragment, withParams);

        return this;
    }

    @Override
    public <R> R executeWith(ResultSetHandler<R> withHandler) throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.parameterizedSql())) {
            sql.fill(preparedStatement);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return withHandler.handle(resultSet);
            }
        }
    }
}
