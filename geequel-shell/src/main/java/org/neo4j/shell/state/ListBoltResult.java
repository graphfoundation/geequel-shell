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
import org.neo4j.driver.v1.summary.ResultSummary;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A fully materialized Cypher result.
 */
public class ListBoltResult implements BoltResult {

    private final List<String> keys;
    private final List<Record> records;
    private final ResultSummary summary;

    public ListBoltResult(@Nonnull List<Record> records, @Nonnull ResultSummary summary) {
        this(records, summary, records.isEmpty() ? Collections.emptyList() : records.get(0).keys());
    }

    public ListBoltResult(@Nonnull List<Record> records, @Nonnull ResultSummary summary, @Nonnull List<String> keys) {
        this.keys = keys;
        this.records = records;
        this.summary = summary;
    }

    @Nonnull
    @Override
    public List<String> getKeys() {
        return keys;
    }

    @Override
    @Nonnull
    public List<Record> getRecords() {
        return records;
    }

    @Override
    @Nonnull
    public Iterator<Record> iterate() {
        return records.iterator();
    }

    @Override
    @Nonnull
    public ResultSummary getSummary() {
        return summary;
    }
}
