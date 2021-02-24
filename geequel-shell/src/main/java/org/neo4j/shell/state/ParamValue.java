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

/**
 * Handles queryparams value and user inputString
 */
public class ParamValue {
    private final String valueAsString;
    private final Object value;

    public ParamValue(String valueAsString, Object value) {
        this.valueAsString = valueAsString;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public String getValueAsString() {
        return valueAsString;
    }
}
