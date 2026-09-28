/*
 * Copyright (c) "Graph Foundation,"
 * Graph Foundation, Inc. [https://graphfoundation.org]
 *
 * This file is part of ONgDB.
 *
 * ONgDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
/*
 * Copyright (c) 2002-2020 "Neo4j,"
 * Neo4j Sweden AB [http://neo4j.com]
 *
 * This file is part of Neo4j.
 *
 * Neo4j is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
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
package org.neo4j.shell;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.shell.cli.CliArgHelper;
import org.neo4j.shell.cli.CliArgs;
import org.neo4j.shell.cli.NonInteractiveShellRunner;
import org.neo4j.shell.log.Logger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.neo4j.shell.ShellRunner.getShellRunner;

public class ShellRunnerTest
{
    @Rule
    public final ExpectedException thrown = ExpectedException.none();
    private final ConnectionConfig connectionConfig = mock( ConnectionConfig.class );

    @Test
    public void inputIsNonInteractiveIfForced() throws Exception
    {
        CliArgs args = new CliArgs();
        args.setNonInteractive( true );
        ShellRunner runner = getShellRunner( args, mock( CypherShell.class ), mock( Logger.class ), connectionConfig );
        assertTrue( "Should be non-interactive shell runner when forced",
                    runner instanceof NonInteractiveShellRunner );
    }

    @Test
    public void inputIsNonInteractiveWithFileArgument()
    {
        assertFalse( "--file input should not be interactive", ShellRunner.isInputInteractive( CliArgHelper.parse( "-f", "some-file" ) ) );
    }

    @Test
    public void fileIsAbsentWithoutFileArgument()
    {
        assertFalse( "no --file given", CliArgHelper.parse( "RETURN 1;" ).getFile().isPresent() );
    }
}
