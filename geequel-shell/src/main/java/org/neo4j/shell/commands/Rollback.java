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

import org.neo4j.shell.TransactionHandler;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.exception.ExitException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static org.neo4j.shell.commands.CommandHelper.simpleArgParse;

/**
 * This command marks a transaction as failed and closes it.
 */
public class Rollback implements Command {
    public static final String COMMAND_NAME = ":rollback";
    private final TransactionHandler transactionHandler;

    public Rollback(@Nonnull final TransactionHandler transactionHandler) {
        this.transactionHandler = transactionHandler;
    }

    @Nonnull
    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Nonnull
    @Override
    public String getDescription() {
        return "Rollback the currently open transaction";
    }

    @Nonnull
    @Override
    public String getUsage() {
        return "";
    }

    @Nonnull
    @Override
    public String getHelp() {
        return "Rolls back and closes the currently open transaction";
    }

    @Nonnull
    @Override
    public List<String> getAliases() {
        return new ArrayList<>();
    }

    @Override
    public void execute(@Nonnull final String argString) throws ExitException, CommandException {
        simpleArgParse(argString, 0, COMMAND_NAME, getUsage());

        transactionHandler.rollbackTransaction();
    }
}
