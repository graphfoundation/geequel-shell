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
package org.neo4j.shell.prettyprint;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CypherVariablesFormatter {
    private static final String BACKTICK = "`";
    private static final Pattern ALPHA_NUMERIC = Pattern.compile("^[\\p{L}_][\\p{L}0-9_]*");

    @Nonnull
    public static String escape(@Nonnull String string) {
        Matcher alphaNumericMatcher = ALPHA_NUMERIC.matcher(string);
        if (!alphaNumericMatcher.matches()) {
            String reEscapeBackTicks = string.replaceAll(BACKTICK, BACKTICK + BACKTICK);
            return BACKTICK + reEscapeBackTicks + BACKTICK;
        }
        return string;
    }

    @Nonnull
    public static String unescapedCypherVariable(@Nonnull String string) {
        Matcher alphaNumericMatcher = ALPHA_NUMERIC.matcher(string);
        if (!alphaNumericMatcher.matches()) {
            String substring = string.substring(1, string.length() - 1);
            return substring.replace(BACKTICK + BACKTICK, BACKTICK);
        } else {
            return string;
        }
    }
}
