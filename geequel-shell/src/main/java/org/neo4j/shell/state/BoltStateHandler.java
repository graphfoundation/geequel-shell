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

import org.neo4j.driver.*;
import org.neo4j.driver.Result;
import org.neo4j.driver.exceptions.SessionExpiredException;
import org.neo4j.shell.ConnectionConfig;
import org.neo4j.shell.Connector;
import org.neo4j.shell.TransactionHandler;
import org.neo4j.shell.TriFunction;
import org.neo4j.shell.config.Build;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.log.NullLogging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Handles interactions with the driver
 */
public class BoltStateHandler implements TransactionHandler, Connector {
    private static final String USER_AGENT = "ongdb-geequel-shell/v" + Build.version();
    private final TriFunction<String, AuthToken, Config, Driver> driverProvider;
    protected Driver driver;
    protected Session session;
    private String version;
    private Transaction tx = null;

    public BoltStateHandler() {
        this(GraphDatabase::driver);
    }

    BoltStateHandler(TriFunction<String, AuthToken, Config, Driver> driverProvider) {
        System.out.println("Creating BoltStateHandler");
        this.driverProvider = driverProvider;
        System.out.println("Created BoltStateHandler");
    }

    @Override
    public void beginTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to ONgDB");
        }
        if (isTransactionOpen()) {
            throw new CommandException("There is already an open transaction");
        }
        tx = session.beginTransaction();
    }

    @Override
    public Optional<List<BoltResult>> commitTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to ONgDB");
        }
        if (!isTransactionOpen()) {
            throw new CommandException("There is no open transaction to commit");
        }
        tx.commit();
        tx.close();
        tx = null;

        return Optional.empty();
    }

    @Override
    public void rollbackTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to ONgDB");
        }
        if (!isTransactionOpen()) {
            throw new CommandException("There is no open transaction to rollback");
        }
        tx.rollback();
        tx.close();
        tx = null;
    }

    @Override
    public boolean isTransactionOpen() {
        return tx != null;
    }

    @Override
    public boolean isConnected() {
        return session != null && session.isOpen();
    }

    @Override
    public void connect(@Nonnull ConnectionConfig connectionConfig) throws CommandException {
        if (isConnected()) {
            throw new CommandException("Already connected");
        }

        final AuthToken authToken = AuthTokens.basic(connectionConfig.username(), connectionConfig.password());

        try {
            System.out.println("About to get driver");
            driver = getDriver(connectionConfig, authToken);
            System.out.println("Got driver");
            reconnect();
            System.out.println("Reconnected");
        } catch (Throwable t) {
            try {
                System.err.println("Error connecting to ONgDB: " + t.getMessage());
                silentDisconnect();
            } catch (Exception e) {
                System.err.println("Error disconnecting from ONgDB: " + e.getMessage());
                t.addSuppressed(e);
            }
            throw t;
        }
    }

    private void reconnect() {
        Bookmark bookmark = null;
        if (session != null) {
            bookmark = session.lastBookmark();
            session.close();
        }
        System.out.println("Reconnecting with bookmark: " + bookmark);
        SessionConfig sessionConfig = SessionConfig.builder().withDefaultAccessMode(AccessMode.WRITE).withBookmarks( bookmark).build();
        session = driver.session(sessionConfig);
        System.out.println("Reconnected with bookmark: " + session);
        Result run = session.run("RETURN 1");
        System.out.println("About to consume result");
        this.version = run.consume().server().version();
        System.out.println("Consumed result, version: " + version);
        run.consume();
    }

    @Nonnull
    @Override
    public String getServerVersion()
    {
        if ( isConnected() )
        {
            if ( version == null )
            {
                // Running unsupported version
                version = "";
            }
            if ( version.startsWith( "ONgDB/" ) || version.startsWith( "Neo4j/" ) )
            {
                // Want to return '1.0.0' and not 'ONgDB/1.0.0' or 'Neo4j/1.0.0'
                version = version.substring( 6 );
            }
            return version;
        }
        return "";
    }

    @Nonnull
    public Optional<BoltResult> runCypher(@Nonnull String cypher,
                                          @Nonnull Map<String, Object> queryParams) throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to ONgDB");
        }
        if (isTransactionOpen()) {
            // If this fails, don't try any funny business - just let it die
            return getBoltResult(cypher, queryParams);
        } else {
            try {
                // Note that PERIODIC COMMIT can't execute in a transaction, so if the user has not typed BEGIN, then
                // the statement should NOT be executed in a transaction.
                return getBoltResult(cypher, queryParams);
            } catch (SessionExpiredException e) {
                // Server is no longer accepting writes, reconnect and try again.
                // If it still fails, leave it up to the user
                reconnect();
                return getBoltResult(cypher, queryParams);
            }
        }
    }

    /**
     * @throws SessionExpiredException when server no longer serves writes anymore
     */
    @Nonnull
    private Optional<BoltResult> getBoltResult(@Nonnull String cypher, @Nonnull Map<String, Object> queryParams) throws SessionExpiredException {
        Result Result;

        if (isTransactionOpen()){
            Result = tx.run(new Query(cypher, queryParams));
        } else {
            Result = session.run(new Query(cypher, queryParams));
        }

        if (Result == null) {
            return Optional.empty();
        }

        return Optional.of(new StatementBoltResult(Result));
    }

    /**
     * Disconnect from ONgDB, clearing up any session resources, but don't give any output.
     * Intended only to be used if connect fails.
     */
    void silentDisconnect() {
        try {
            if (session != null) {
                session.close();
            }
            if (driver != null) {
                driver.close();
            }
        } finally {
            session = null;
            driver = null;
        }
    }

    /**
     * Reset the current session. This rolls back any open transactions.
     */
    public void reset() {
        if (isConnected()) {
            session.reset();

            // Clear current state
            if (isTransactionOpen()) {
                // Bolt has already rolled back the transaction but it doesn't close it properly
                tx.rollback();
                tx.close();
                tx = null;
            }
        }
    }

    private Driver getDriver(@Nonnull ConnectionConfig connectionConfig, @Nullable AuthToken authToken) {
        Config config = Config.builder()
                              .withLogging(NullLogging.NULL_LOGGING).withUserAgent( USER_AGENT ).build();

        System.out.println("About to get driver with authToken: " + authToken);
        String driverUrl = connectionConfig.driverUrl();
        System.out.println("About to get driver with url: " + driverUrl);
        return driverProvider.apply(driverUrl, authToken, config);
    }

    private List<BoltResult> executeWithRetry(List<Query> transactionStatements, BiFunction<Query, Transaction, BoltResult> biFunction) {
        return session.writeTransaction(tx ->
                transactionStatements.stream()
                        .map(transactionStatement -> biFunction.apply(transactionStatement, tx))
                        .collect(Collectors.toList()));

    }
}
