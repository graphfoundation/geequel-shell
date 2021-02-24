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

import org.neo4j.shell.commands.Exit;
import org.neo4j.shell.commands.Help;
import org.neo4j.shell.log.AnsiFormattedText;

import javax.annotation.Nonnull;

public class UserMessagesHandler {
    private ConnectionConfig connectionConfig;
    private String serverVersion;

    public UserMessagesHandler(@Nonnull ConnectionConfig connectionConfig, @Nonnull String serverVersion) {
        this.connectionConfig = connectionConfig;
        this.serverVersion = serverVersion;
    }

    @Nonnull
    public String getWelcomeMessage() {
        String ongdb = "ONgDB";
        if (!serverVersion.isEmpty()) {
            ongdb += " " + serverVersion;
        }
        AnsiFormattedText welcomeMessage = AnsiFormattedText.from("Connected to ")
                                                            .append(ongdb)
                                                            .append(" at ")
                                                            .bold().append(connectionConfig.driverUrl()).boldOff();

        if (!connectionConfig.username().isEmpty()) {
            welcomeMessage = welcomeMessage
                    .append(" as user ")
                    .bold().append(connectionConfig.username()).boldOff();
        }

        return welcomeMessage
                .append(".\nType ")
                .bold().append(Help.COMMAND_NAME).boldOff()
                .append(" for a list of available commands or ")
                .bold().append(Exit.COMMAND_NAME).boldOff()
                .append(" to exit the shell.")
                .append("\nNote that Geequel queries must end with a ")
                .bold().append("semicolon.").boldOff().formattedString();
    }

    @Nonnull
    public String getExitMessage() {
        return AnsiFormattedText.s().append("\nBye!").formattedString();
    }
}
