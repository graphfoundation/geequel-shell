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
package org.neo4j.shell.state;

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.summary.ResultSummary;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

/**
 * Wrapper around {@link StatementResult}. Might or might not be materialized.
 */
public class StatementBoltResult implements BoltResult {

    private final StatementResult result;

    public StatementBoltResult(StatementResult result) {
        this.result = result;
    }

    @Nonnull
    @Override
    public List<String> getKeys() {
        return result.keys();
    }

    @Nonnull
    @Override
    public List<Record> getRecords() {
        return result.list();
    }

    @Nonnull
    @Override
    public Iterator<Record> iterate() {
        return result;
    }

    @Nonnull
    @Override
    public ResultSummary getSummary() {
        return result.summary();
    }
}
