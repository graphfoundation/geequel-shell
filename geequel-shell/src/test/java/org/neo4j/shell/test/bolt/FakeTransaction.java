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

import org.neo4j.driver.*;
import org.neo4j.driver.types.TypeSystem;

import java.util.concurrent.CompletionStage;
import java.util.Map;

public class FakeTransaction implements Transaction {
    @Override
    public boolean isOpen() {
        return true;
    }


    @Override
    public void commit() {
    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }

    @Override
    public Result run(String query, Value parameters) {
        return FakeResult.parseStatement(query);
    }

    @Override
    public Result run(String query, Map<String, Object> parameters) {
        return FakeResult.parseStatement(query);
    }

    @Override
    public Result run(String query, Record parameters) {
        return FakeResult.parseStatement(query);
    }

    @Override
    public Result run(String query) {
        return FakeResult.parseStatement(query);
    }

    @Override
    public Result run(Query query) {
        return FakeResult.parseStatement(query.text());
    }

}
