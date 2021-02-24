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
package org.neo4j.shell.commands;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.driver.v1.exceptions.AuthenticationException;
import org.neo4j.shell.CypherShell;
import org.neo4j.shell.StringLinePrinter;
import org.neo4j.shell.cli.Format;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.prettyprint.PrettyConfig;

public class CypherShellFailureIntegrationTest extends CypherShellIntegrationTest
{
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final StringLinePrinter linePrinter = new StringLinePrinter();

    @Before
    public void setUp()
    {
        linePrinter.clear();
        shell = new CypherShell( linePrinter, new PrettyConfig( Format.VERBOSE, true, 1000 ) );
    }

    @Test
    public void cypherWithNoPasswordShouldReturnValidError() throws CommandException
    {
        thrown.expect( AuthenticationException.class );
        thrown.expectMessage( "The client is unauthorized due to authentication failure." );

        connect( "" );
    }
}
