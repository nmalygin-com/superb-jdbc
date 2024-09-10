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

/**
 * {@link Change} factory.
 *
 * <p>
 * Example of use:
 * <pre>{@code
 *   Change change = changes.change(
 *                 "INSERT INTO books (id, title) VALUES (?, ?)",
 *                 new IntParam(1),
 *                 new StringParam("Clean Code"));
 * }</pre>
 *
 * @author Nikolai Malygin
 */
public interface Changes {

    /**
     * Creates a {@link Change} object.
     *
     * @param sql Parameterized modify query (DML or DDL)
     * @param withArguments Values for the corresponding parameters
     * @return The {@link Change} object
     */
    Change change(String sql, Argument... withArguments);
}
