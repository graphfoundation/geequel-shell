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

import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.neo4j.driver.v1.summary.ResultSummary;
import org.neo4j.driver.v1.util.Function;
import org.neo4j.shell.test.Util;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A fake StatementResult with fake records and fake values
 */
class FakeStatementResult implements StatementResult {

    private final List<Record> records;
    private int currentRecord = -1;

    FakeStatementResult() {
        records = new ArrayList<>();
    }

    @Override
    public List<String> keys() {
        return records.stream().map(r -> r.keys().get(0)).collect(Collectors.toList());
    }

    @Override
    public boolean hasNext() {
        return currentRecord + 1 < records.size();
    }

    @Override
    public Record next() {
        currentRecord += 1;
        return records.get(currentRecord);
    }

    @Override
    public Record single() throws NoSuchRecordException {
        if (records.size() == 1) {
            return records.get(0);
        }
        throw new NoSuchRecordException("There are more than records");
    }

    @Override
    public Record peek() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public Stream<Record> stream()
    {
        return records.stream();
    }

    @Override
    public List<Record> list() {
        return records;
    }

    @Override
    public <T> List<T> list(Function<Record, T> mapFunction) {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public ResultSummary consume() {
        return new FakeResultSummary();
    }

    @Override
    public ResultSummary summary()
    {
        return new FakeResultSummary();
    }

    /**
     * Supports fake parsing of very limited cypher statements, only for basic test purposes
     */
    static FakeStatementResult parseStatement(@Nonnull final String statement) {

        Pattern returnAsPattern = Pattern.compile("^return (.*) as (.*)$", Pattern.CASE_INSENSITIVE);
        Pattern returnPattern = Pattern.compile("^return (.*)$", Pattern.CASE_INSENSITIVE);

        // Be careful with order here
        for (Pattern p: Arrays.asList(returnAsPattern, returnPattern)) {
            Matcher m = p.matcher(statement);
            if (m.find()) {
                String value = m.group(1);
                String key = value;
                if (m.groupCount() > 1) {
                    key = m.group(2);
                }
                FakeStatementResult statementResult = new FakeStatementResult();
                statementResult.records.add(FakeRecord.of(key, value));
                return statementResult;
            }
        }
        throw new IllegalArgumentException("No idea how to parse this statement");
    }
}
