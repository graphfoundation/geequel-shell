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

import org.neo4j.driver.internal.summary.InternalSummaryCounters;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.summary.*;
import org.neo4j.shell.test.Util;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * A fake result summary
 */
class FakeResultSummary implements ResultSummary {
    @Override
    public Statement statement() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public SummaryCounters counters() {
        return InternalSummaryCounters.EMPTY_STATS;
    }

    @Override
    public StatementType statementType() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public boolean hasPlan() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public boolean hasProfile() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public Plan plan() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public ProfiledPlan profile() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public List<Notification> notifications() {
        throw new Util.NotImplementedYetException("Not implemented yet");
    }

    @Override
    public long resultAvailableAfter(TimeUnit unit) {
        return 0;
    }

    @Override
    public long resultConsumedAfter(TimeUnit unit) {
        return 0;
    }

    @Override
    public ServerInfo server()
    {
        return new ServerInfo()
        {
            @Override
            public String address()
            {
                throw new Util.NotImplementedYetException("Not implemented yet");
            }

            @Override
            public String version()
            {
                return null;
            }
        };
    }
}
