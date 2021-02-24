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
package org.neo4j.shell.test;

public class Util {
    public static String[] asArray(String... arguments) {
        return arguments;
    }

    public static class NotImplementedYetException extends RuntimeException {
        public NotImplementedYetException(String message) {
            super(message);
        }
    }

    /**
     * Generate the control code for the specified character. For example, give this method 'C', and it will return
     * the code for `Ctrl-C`, which you can append to an inputbuffer for example, in order to simulate the  user
     * pressing Ctrl-C.
     *
     * @param let character to generate code for, must be between A and Z
     * @return control code for given character
     */
    public static char ctrl(final char let) {
        if (let < 'A' || let > 'Z') {
            throw new IllegalArgumentException("Cannot generate CTRL code for "
                    + "char '" + let + "' (" + ((int) let) + ")");
        }

        int result = ((int) let) - 'A' + 1;
        return (char) result;
    }
}
