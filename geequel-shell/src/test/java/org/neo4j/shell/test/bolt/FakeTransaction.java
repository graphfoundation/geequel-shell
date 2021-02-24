/*
 * Copyright (c) 2018-2020 "Graph Foundation"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it underm the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.neo4j.shell.test.bolt;

import org.neo4j.driver.v1.*;
import org.neo4j.driver.v1.types.TypeSystem;

import java.util.concurrent.CompletionStage;
import java.util.Map;

public class FakeTransaction implements Transaction {
    @Override
    public boolean isOpen() {
        return true;
    }

    @Override
    public void success() {

    }

    @Override
    public void failure() {

    }

    @Override
    public void close() {

    }

    @Override
    public CompletionStage<Void> commitAsync() {
        return null;
    }

    @Override
    public CompletionStage<Void> rollbackAsync() {
        return null;
    }

    @Override
    public StatementResult run(String query, Value parameters) {
        return null;
    }

    @Override
    public StatementResult run(String query, Map<String, Object> parameters) {
        return null;
    }

    @Override
    public StatementResult run(String query, Record parameters) {
        return null;
    }

    @Override
    public StatementResult run(String query) {
        return null;
    }

    @Override
    public StatementResult run(Statement statement) {
        return null;
    }

    @Override
    public TypeSystem typeSystem() {
        return null;
    }

    @Override
    public CompletionStage<StatementResultCursor> runAsync( String statement, Map<String,Object> parameters)
    {
        return null;
    }

    @Override
    public CompletionStage<StatementResultCursor> runAsync(String statementTemplate, Value parameters) {
        return null;
    }

    @Override
    public CompletionStage<StatementResultCursor> runAsync(String statementTemplate, Record statementParameters) {
        return null;
    }

    @Override
    public CompletionStage<StatementResultCursor> runAsync(String statementTemplate) {
        return null;
    }

    @Override
    public CompletionStage<StatementResultCursor> runAsync(Statement statement) {
        return null;
    }
}
