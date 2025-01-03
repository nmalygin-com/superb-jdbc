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

import com.nmalygin.superb.jdbc.api.Argument;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class NotThreadSafeSql implements Sql {

    @SuppressWarnings("PMD.AvoidStringBufferField")
    private final StringBuilder stringBuilder;
    private final List<Argument> arguments;

    NotThreadSafeSql(final StringBuilder stringBuilder, final List<Argument> arguments) {
        this.stringBuilder = stringBuilder;
        this.arguments = arguments;
    }

    NotThreadSafeSql(final String sqlFragment, final Argument... withArguments) {
        this(new StringBuilder(sqlFragment), new ArrayList<>(Arrays.asList(withArguments)));
    }

    @Override
    public void append(final String sqlFragment, final Argument... withArguments) {
        stringBuilder.append(sqlFragment);
        arguments.addAll(Arrays.asList(withArguments));
    }

    @Override
    public String parameterizedSql() {
        return stringBuilder.toString();
    }

    @Override
    public void fill(final PreparedStatement preparedStatement) throws SQLException {
        int index = 1;
        for (final Argument argument : arguments) {
            argument.pass(preparedStatement, index++);
        }
    }
}
