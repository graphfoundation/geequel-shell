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
package org.neo4j.shell;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserMessagesHandlerTest {
    private final ConnectionConfig connectionConfig = mock(ConnectionConfig.class);

    @Test
    public void welcomeMessageTest() {
        when(connectionConfig.username()).thenReturn("bob");
        when(connectionConfig.driverUrl()).thenReturn("bolt://some.place.com:99");

        UserMessagesHandler userMessagesHandler = new UserMessagesHandler(connectionConfig, "1.0.0-alpha01");
        assertEquals("Connected to ONgDB at @|BOLD bolt://some.place.com:99|@ as user @|BOLD bob|@.\n" +
                        "Type @|BOLD :help|@ for a list of available commands or @|BOLD :exit|@ to exit the shell.\n" +
                        "Note that Geequel queries must end with a @|BOLD semicolon.|@",
                userMessagesHandler.getWelcomeMessage());
    }

    @Test
    public void exitMessageTest() {
        when(connectionConfig.username()).thenReturn("bob");
        when(connectionConfig.driverUrl()).thenReturn("bolt://some.place.com:99");

        UserMessagesHandler userMessagesHandler = new UserMessagesHandler(connectionConfig, "1.0.0-alpha01");
        assertEquals("\nBye!", userMessagesHandler.getExitMessage());
    }
}
