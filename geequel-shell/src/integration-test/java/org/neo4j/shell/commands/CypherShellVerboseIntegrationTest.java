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

import org.apache.commons.lang3.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.neo4j.shell.CypherShell;
import org.neo4j.shell.StringLinePrinter;
import org.neo4j.shell.cli.Format;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.prettyprint.PrettyConfig;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;
import static org.neo4j.shell.Versions.majorVersion;
import static org.neo4j.shell.Versions.minorVersion;

public class CypherShellVerboseIntegrationTest extends CypherShellIntegrationTest
{
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private StringLinePrinter linePrinter = new StringLinePrinter();
    private Command rollbackCommand;
    private Command commitCommand;
    private Command beginCommand;

    @Before
    public void setUp() throws Exception
    {
        linePrinter.clear();
        shell = new CypherShell( linePrinter, new PrettyConfig( Format.VERBOSE, true, 1000 ) );
        rollbackCommand = new Rollback( shell );
        commitCommand = new Commit( shell );
        beginCommand = new Begin( shell );

        connect( "ongdb" );
    }

    @After
    public void tearDown() throws Exception
    {
        shell.execute( "MATCH (n) DETACH DELETE (n)" );
    }

    @Test
    public void cypherWithNoReturnStatements() throws CommandException
    {
        //when
        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\"})" );

        //then
        assertThat( linePrinter.output(), containsString( "Added 1 nodes, Set 1 properties, Added 1 labels" ) );
    }

    @Test
    public void cypherWithReturnStatements() throws CommandException
    {
        //when
        shell.execute( "CREATE (jane :TestPerson {name: \"Jane Smith\"}) RETURN jane" );

        //then
        String output = linePrinter.output();
        assertThat( output, containsString( "| jane " ) );
        assertThat( output, containsString( "| (:TestPerson {name: \"Jane Smith\"}) |" ) );
        assertThat( output, containsString( "Added 1 nodes, Set 1 properties, Added 1 labels" ) );
    }

    @Test
    public void connectTwiceThrows() throws CommandException
    {
        thrown.expect( CommandException.class );
        thrown.expectMessage( "Already connected" );

        assertTrue( "Shell should already be connected", shell.isConnected() );
        connect( "ongdb" );
    }

    @Test
    public void rollbackScenario() throws CommandException
    {
        //given
        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\"})" );

        //when
        beginCommand.execute( "" );
        shell.execute( "CREATE (:NotCreated)" );
        rollbackCommand.execute( "" );

        //then
        shell.execute( "MATCH (n) RETURN n" );

        String output = linePrinter.output();
        assertThat( output, containsString( "| n " ) );
        assertThat( output, containsString( "| (:TestPerson {name: \"Jane Smith\"}) |" ) );
        assertThat( output, not( containsString( ":NotCreated" ) ) );
    }

    @Test
    public void resetOutOfTxScenario() throws CommandException
    {
        //when
        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\"})" );
        shell.reset();

        //then
        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\"})" );
        shell.execute( "MATCH (n:TestPerson) RETURN n ORDER BY n.name" );

        String result = linePrinter.output();
        assertThat( result, containsString(
                "| (:TestPerson {name: \"Jane Smith\"}) |\n" +
                "| (:TestPerson {name: \"Jane Smith\"}) |" ) );
    }

    @Test
    public void resetInTxScenario() throws CommandException
    {
        //when
        beginCommand.execute( "" );
        shell.execute( "CREATE (:NotCreated)" );
        shell.reset();

        //then
        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\"})" );
        shell.execute( "MATCH (n) RETURN n" );

        String result = linePrinter.output();
        assertThat( result, containsString( "| (:TestPerson {name: \"Jane Smith\"}) |" ) );
        assertThat( result, not( containsString( ":NotCreated" ) ) );
    }

    @Test
    public void commitScenario() throws CommandException
    {
        beginCommand.execute( "" );
        shell.execute( "CREATE (:TestPerson {name: \"Joe Smith\"})" );
        // Here we assert that text is printed before commit on explicit transactions.
        assertThat( StringUtils.split( linePrinter.output(), '\n' ).length, equalTo( 2 ) );
        assertThat( linePrinter.output(), containsString( "\nAdded 1 nodes, Set 1 properties, Added 1 labels\n" ) );

        shell.execute( "CREATE (:TestPerson {name: \"Jane Smith\", born: 1990})" );
        assertThat( StringUtils.split( linePrinter.output(), '\n' ).length, equalTo( 4 ) );
        assertThat( linePrinter.output(), containsString( "\nAdded 1 nodes, Set 2 properties, Added 1 labels\n" ) );

        shell.execute( "MATCH (n:TestPerson) RETURN n ORDER BY n.name" );
        assertThat( linePrinter.output(),
                    containsString( "\n| (:TestPerson {name: \"Jane Smith\", born: 1990}) |\n| (:TestPerson {name: \"Joe Smith\"})              |\n" ) );

        commitCommand.execute( "" );

        // then
        String result = linePrinter.output();
        assertThat( result,
                    containsString( "\n| (:TestPerson {name: \"Jane Smith\", born: 1990}) |\n| (:TestPerson {name: \"Joe Smith\"})              |\n" ) );
    }

    @Test
    public void paramsAndListVariables() throws CommandException
    {
        assertTrue( shell.allParameterValues().isEmpty() );

        long randomLong = System.currentTimeMillis();
        String stringInput = "\"randomString\"";
        shell.setParameter( "string", stringInput );
        Object paramValue = shell.setParameter( "bob", String.valueOf( randomLong ) );
        assertEquals( randomLong, paramValue );

        shell.execute( "RETURN $bob, $string" );

        String result = linePrinter.output();
        assertThat( result, containsString( "| $bob" ) );
        assertThat( result, containsString( "| " + randomLong + " | " + stringInput + " |" ) );
        assertEquals( randomLong, shell.allParameterValues().get( "bob" ) );
        assertEquals( "randomString", shell.allParameterValues().get( "string" ) );
    }

    @Test
    public void paramsAndListVariablesWithSpecialCharacters() throws CommandException
    {
        assertTrue( shell.allParameterValues().isEmpty() );

        long randomLong = System.currentTimeMillis();
        Object paramValue = shell.setParameter( "`bob`", String.valueOf( randomLong ) );
        assertEquals( randomLong, paramValue );

        shell.execute( "RETURN $`bob`" );

        String result = linePrinter.output();
        assertThat( result, containsString( "| $`bob`" ) );
        assertThat( result, containsString( "\n| " + randomLong + " |\n" ) );
        assertEquals( randomLong, shell.allParameterValues().get( "bob" ) );
    }

    @Test
    public void cypherWithOrder() throws CommandException
    {
        // given
        String serverVersion = shell.getServerVersion();
        assumeTrue( minorVersion( serverVersion ) == 6 || majorVersion( serverVersion ) == 4 );

        shell.execute( "CREATE INDEX ON :Person(age)" );
        shell.execute( "CALL db.awaitIndexes()" );

        //when
        shell.execute( "CYPHER RUNTIME=INTERPRETED EXPLAIN MATCH (n:Person) WHERE n.age >= 18 RETURN n.name, n.age" );

        //then
        String actual = linePrinter.output();
        assertThat( actual, containsString( "Ordered by" ) );
        assertThat( actual, containsString( "n.age ASC" ) );
    }

    @Test
    public void cypherWithExplainAndRulePlanner() throws CommandException
    {
        //given (there is no rule planner in ongdb 2.0)
        assumeTrue( majorVersion( shell.getServerVersion() ) < 4 );

        //when
        shell.execute( "CYPHER planner=rule EXPLAIN MATCH (e:E) WHERE e.bucket='Live' and e.id = 23253473 RETURN count(e)" );

        //then
        String actual = linePrinter.output();
        assertThat( actual, containsString( "\"EXPLAIN\"" ) );
        assertThat( actual, containsString( "\"READ_ONLY\"" ) );
        assertThat( actual, containsString( "\"RULE\"" ) );
        assertThat( actual, containsString( "\"INTERPRETED\"" ) );
    }

    @Test
    public void shouldShowTheNumberOfRows() throws CommandException
    {
        //when
        shell.execute( "UNWIND [1,2,3] AS row RETURN row" );

        //then
        String actual = linePrinter.output();
        assertThat( actual, containsString( "3 rows available" ) );
    }

    @Test
    public void shouldNotContainUnnecessaryNewLines() throws CommandException
    {
        //when
        shell.execute( "UNWIND [1,2,3] AS row RETURN row" );

        //then
        String actual = linePrinter.output();
        assertThat( actual,
                    containsString( String.format(
                            "+-----+%n" +
                            "| row |%n" +
                            "+-----+%n" +
                            "| 1   |%n" +
                            "| 2   |%n" +
                            "| 3   |%n" +
                            "+-----+%n" +
                            "%n" +
                            "3 rows available after" ) ) );
    }
}