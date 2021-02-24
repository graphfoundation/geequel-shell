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
package org.neo4j.shell.parser;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * An object capable of parsing a piece of text and returning a List statements contained within.
 */
public interface StatementParser {

    /**
     * Parse the next line of text
     *
     * @param line to parse
     */
    void parseMoreText(@Nonnull String line);

    /**
     * @return true if any statements have been parsed yet, false otherwise
     */
    boolean hasStatements();

    /**
     * Once this method has been called, the method will return the empty list (unless more text is parsed).
     * If nothing has been parsed yet, then the empty list is returned.
     *
     * @return statements which have been parsed so far and remove them from internal state
     */
    @Nonnull
    List<String> consumeStatements();

    /**
     * @return false if no text (except whitespace) has been seen since last parsed statement, true otherwise.
     */
    boolean containsText();

    /**
     * Reset the state of the Parser, removing any and all state it has.
     */
    void reset();
}
