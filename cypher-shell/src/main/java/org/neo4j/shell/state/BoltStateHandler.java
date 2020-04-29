package org.neo4j.shell.state;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.AuthToken;
import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Config;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.exceptions.SessionExpiredException;
import org.neo4j.shell.ConnectionConfig;
import org.neo4j.shell.Connector;
import org.neo4j.shell.TransactionHandler;
import org.neo4j.shell.TriFunction;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.log.NullLogging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Handles interactions with the driver
 */
public class BoltStateHandler implements TransactionHandler, Connector {
    private final TriFunction<String, AuthToken, Config, Driver> driverProvider;
    protected Driver driver;
    protected Session session;
    private String version;
    private Transaction tx = null;

    public BoltStateHandler() {
        this(GraphDatabase::driver);
    }

    BoltStateHandler(TriFunction<String, AuthToken, Config, Driver> driverProvider) {
        this.driverProvider = driverProvider;
    }

    @Override
    public void beginTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to Neo4j");
        }
        if (isTransactionOpen()) {
            throw new CommandException("There is already an open transaction");
        }
        tx = session.beginTransaction();
    }

    @Override
    public Optional<List<BoltResult>> commitTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to Neo4j");
        }
        if (!isTransactionOpen()) {
            throw new CommandException("There is no open transaction to commit");
        }
        tx.success();
        tx.close();
        tx = null;

        return Optional.empty();
    }

    @Override
    public void rollbackTransaction() throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to Neo4j");
        }
        if (!isTransactionOpen()) {
            throw new CommandException("There is no open transaction to rollback");
        }
        tx.failure();
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
            driver = getDriver(connectionConfig, authToken);
            reconnect();
        } catch (Throwable t) {
            try {
                silentDisconnect();
            } catch (Exception e) {
                t.addSuppressed(e);
            }
            throw t;
        }
    }

    private void reconnect() {
        String bookmark = null;
        if (session != null) {
            bookmark = session.lastBookmark();
            session.close();
        }
        session = driver.session(AccessMode.WRITE, bookmark);
        StatementResult run = session.run("RETURN 1");
        this.version = run.summary().server().version();
        run.consume();
    }

    @Nonnull
    @Override
    public String getServerVersion() {
        if (isConnected()) {
            if (version == null) {
                // On versions before 3.1.0-M09
                version = "";
            }
            if (version.startsWith("Neo4j/")) {
                // Want to return '3.1.0' and not 'Neo4j/3.1.0'
                version = version.substring(6);
            }
            return version;
        }
        return "";
    }

    @Nonnull
    public Optional<BoltResult> runCypher(@Nonnull String cypher,
                                          @Nonnull Map<String, Object> queryParams) throws CommandException {
        if (!isConnected()) {
            throw new CommandException("Not connected to Neo4j");
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
        StatementResult statementResult;

        if (isTransactionOpen()){
            statementResult = tx.run(new Statement(cypher, queryParams));
        } else {
            statementResult = session.run(new Statement(cypher, queryParams));
        }

        if (statementResult == null) {
            return Optional.empty();
        }

        return Optional.of(new StatementBoltResult(statementResult));
    }

    /**
     * Disconnect from Neo4j, clearing up any session resources, but don't give any output.
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
                tx.failure();
                tx.close();
                tx = null;
            }
        }
    }

    private Driver getDriver(@Nonnull ConnectionConfig connectionConfig, @Nullable AuthToken authToken) {
        Config config = Config.build()
                              .withLogging(NullLogging.NULL_LOGGING)
                              .withEncryptionLevel(connectionConfig.encryption()).toConfig();
        return driverProvider.apply(connectionConfig.driverUrl(), authToken, config);
    }

    private List<BoltResult> executeWithRetry(List<Statement> transactionStatements, BiFunction<Statement, Transaction, BoltResult> biFunction) {
        return session.writeTransaction(tx ->
                transactionStatements.stream()
                        .map(transactionStatement -> biFunction.apply(transactionStatement, tx))
                        .collect(Collectors.toList()));

    }
}
