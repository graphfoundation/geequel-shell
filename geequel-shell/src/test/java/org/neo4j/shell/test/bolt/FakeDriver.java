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

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.exceptions.Neo4jException;

import java.util.concurrent.CompletionStage;

public class FakeDriver implements Driver {
    @Override
    public boolean isEncrypted() {
        return false;
    }

    @Override
    public Session session() {
        return new FakeSession();
    }

    @Override
    public Session session(AccessMode mode) {
        return new FakeSession();
    }

    @Override
    public Session session(String bookmark) {
        return new FakeSession();
    }

    @Override
    public Session session(AccessMode mode, String bookmark) {
        return new FakeSession();
    }

    @Override
    public Session session(Iterable<String> bookmarks) {
        return new FakeSession();
    }

    @Override
    public Session session(AccessMode mode, Iterable<String> bookmarks) {
        return new FakeSession();
    }

    @Override
    public void close() throws Neo4jException {
    }

    @Override
    public CompletionStage<Void> closeAsync() {
        return null;
    }
}
