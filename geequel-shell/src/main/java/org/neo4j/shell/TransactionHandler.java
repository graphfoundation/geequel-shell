package org.neo4j.shell;

import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.state.BoltResult;

import java.util.List;
import java.util.Optional;

/**
 * An object capable of starting, committing, and rolling back transactions.
 */
public interface TransactionHandler {

    /**
     *
     * @throws CommandException if a new transaction could not be started
     */
    void beginTransaction() throws CommandException;

    /**
     *
     * @throws CommandException if current transaction could not be committed
     */
    Optional<List<BoltResult>> commitTransaction() throws CommandException;

    /**
     *
     * @throws CommandException if current transaction could not be rolled back
     */
    void rollbackTransaction() throws CommandException;

    /**
     * @return true if a transaction is currently open, false otherwise
     */
    boolean isTransactionOpen();
}
