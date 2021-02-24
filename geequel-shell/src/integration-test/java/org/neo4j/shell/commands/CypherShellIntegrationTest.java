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

import org.neo4j.driver.v1.exceptions.ServiceUnavailableException;
import org.neo4j.shell.ConnectionConfig;
import org.neo4j.shell.CypherShell;
import org.neo4j.shell.exception.CommandException;

abstract class CypherShellIntegrationTest
{
    CypherShell shell;

    void connect( String password ) throws CommandException
    {
        // Try with encryption first, which is the default for 1.x
        try
        {
            shell.connect( new ConnectionConfig( "bolt://", "localhost", 7687, "ongdb", password, true ) );
        }
        catch ( ServiceUnavailableException e )
        {
            // This means we are probably in 2.x, let's retry with encryption off
            shell.connect( new ConnectionConfig( "bolt://", "localhost", 7687, "ongdb", password, false ) );
        }
    }
}
