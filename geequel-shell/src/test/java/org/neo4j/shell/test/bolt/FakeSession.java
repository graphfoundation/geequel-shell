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

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * A fake session which returns fake Results
 */
public class FakeSession implements Session {
    private boolean open = true;

    @Override
    public Transaction beginTransaction() {
        return new FakeTransaction();
    }

    @Override
    public Transaction beginTransaction(TransactionConfig config) {
        return null;
    }


    @Override
    public <T> T readTransaction(TransactionWork<T> work) {
        return null;
    }

    @Override
    public <T> T readTransaction(TransactionWork<T> work, TransactionConfig config) {
        return null;
    }

    @Override
    public <T> T writeTransaction(TransactionWork<T> work) {
        return null;
    }

    @Override
    public <T> T writeTransaction(TransactionWork<T> work, TransactionConfig config) {
        return null;
    }

    @Override
    public Result run(String statement, TransactionConfig config) {
        return FakeResult.parseStatement(statement);
    }

    @Override
    public Result run(String statement, Map<String, Object> parameters, TransactionConfig config) {
        return FakeResult.parseStatement(statement);
    }

    @Override
    public Result run(Query query, TransactionConfig transactionConfig) {
        return FakeResult.parseStatement(query.text());
    }

    @Override
    public Bookmark lastBookmark() {
        return null;
    }

    @Override
    public void reset() {
    }

    @Override
    public boolean isOpen() {
        return open;
    }

    @Override
    public void close() {
        open = false;
    }


    @Override
    public Result run(String statementTemplate, Value parameters) {
        return FakeResult.parseStatement(statementTemplate);
    }

    @Override
    public Result run(String statementTemplate, Map<String, Object> statementParameters) {
        return FakeResult.parseStatement(statementTemplate);
    }


    @Override
    public Result run(String statementTemplate, Record statementParameters) {
        return FakeResult.parseStatement(statementTemplate);
    }

    @Override
    public Result run(String statementTemplate) {
        return FakeResult.parseStatement(statementTemplate);
    }

    @Override
    public Result run(Query query) {
        return FakeResult.parseStatement(query.text());
    }
}
