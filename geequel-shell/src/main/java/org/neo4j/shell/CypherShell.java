package org.neo4j.shell;

import org.neo4j.cypher.internal.evaluator.EvaluationException;
import org.neo4j.cypher.internal.evaluator.Evaluator;
import org.neo4j.cypher.internal.evaluator.ExpressionEvaluator;
import org.neo4j.shell.commands.Command;
import org.neo4j.shell.commands.CommandExecutable;
import org.neo4j.shell.commands.CommandHelper;
import org.neo4j.shell.exception.CommandException;
import org.neo4j.shell.exception.ExitException;
import org.neo4j.shell.prettyprint.CypherVariablesFormatter;
import org.neo4j.shell.prettyprint.LinePrinter;
import org.neo4j.shell.prettyprint.PrettyConfig;
import org.neo4j.shell.prettyprint.PrettyPrinter;
import org.neo4j.shell.state.BoltResult;
import org.neo4j.shell.state.BoltStateHandler;
import org.neo4j.shell.state.ParamValue;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A possibly interactive shell for evaluating cypher statements.
 */
public class CypherShell implements StatementExecuter, Connector, TransactionHandler, ParameterMap {
    // Final space to catch newline
    protected static final Pattern cmdNamePattern = Pattern.compile("^\\s*(?<name>[^\\s]+)\\b(?<args>.*)\\s*$");
    protected final Map<String, ParamValue> queryParams = new HashMap<>();
    private final LinePrinter linePrinter;
    private final BoltStateHandler boltStateHandler;
    private final PrettyPrinter prettyPrinter;
    private CommandHelper commandHelper;
    private ExpressionEvaluator evaluator = Evaluator.expressionEvaluator();

    public CypherShell(@Nonnull LinePrinter linePrinter, @Nonnull PrettyConfig prettyConfig) {
        this(linePrinter, new BoltStateHandler(), new PrettyPrinter(prettyConfig));
    }

    protected CypherShell(@Nonnull LinePrinter linePrinter,
                          @Nonnull BoltStateHandler boltStateHandler,
                          @Nonnull PrettyPrinter prettyPrinter) {
        this.linePrinter = linePrinter;
        this.boltStateHandler = boltStateHandler;
        this.prettyPrinter = prettyPrinter;
        addRuntimeHookToResetShell();
    }

    /**
     * @param text to trim
     * @return text without trailing semicolons
     */
    protected static String stripTrailingSemicolons(@Nonnull String text) {
        int end = text.length();
        while (end > 0 && text.substring(0, end).endsWith(";")) {
            end -= 1;
        }
        return text.substring(0, end);
    }

    @Override
    public void execute(@Nonnull final String cmdString) throws ExitException, CommandException {
        // See if it's a shell command
        final Optional<CommandExecutable> cmd = getCommandExecutable(cmdString);
        if (cmd.isPresent()) {
            executeCmd(cmd.get());
            return;
        }

        // Else it will be parsed as Cypher, but for that we need to be connected
        if (!isConnected()) {
            throw new CommandException("Not connected to Neo4j");
        }

        executeCypher(cmdString);
    }

    /**
     * Executes a piece of text as if it were Cypher. By default, all of the cypher is executed in single statement
     * (with an implicit transaction).
     *
     * @param cypher non-empty cypher text to executeLine
     */
    private void executeCypher(@Nonnull final String cypher) throws CommandException {
        final Optional<BoltResult> result = boltStateHandler.runCypher(cypher, allParameterValues());
        result.ifPresent(boltResult -> prettyPrinter.format(boltResult, linePrinter));
    }

    @Override
    public boolean isConnected() {
        return boltStateHandler.isConnected();
    }

    @Nonnull
    protected Optional<CommandExecutable> getCommandExecutable(@Nonnull final String line) {
        Matcher m = cmdNamePattern.matcher(line);
        if (commandHelper == null || !m.matches()) {
            return Optional.empty();
        }

        String name = m.group("name");
        String args = m.group("args");

        Command cmd = commandHelper.getCommand(name);

        if (cmd == null) {
            return Optional.empty();
        }

        return Optional.of(() -> cmd.execute(stripTrailingSemicolons(args)));
    }

    protected void executeCmd(@Nonnull final CommandExecutable cmdExe) throws ExitException, CommandException {
        cmdExe.execute();
    }

    /**
     * Open a session to Neo4j
     *
     * @param connectionConfig
     */
    @Override
    public void connect(@Nonnull ConnectionConfig connectionConfig) throws CommandException {
        boltStateHandler.connect(connectionConfig);
    }

    @Nonnull
    @Override
    public String getServerVersion() {
        return boltStateHandler.getServerVersion();
    }

    @Override
    public void beginTransaction() throws CommandException {
        boltStateHandler.beginTransaction();
    }

    @Override
    public Optional<List<BoltResult>> commitTransaction() throws CommandException {
        Optional<List<BoltResult>> results = boltStateHandler.commitTransaction();
        results.ifPresent(boltResult -> boltResult.forEach(result -> prettyPrinter.format(result, linePrinter)));
        return results;
    }

    @Override
    public void rollbackTransaction() throws CommandException {
        boltStateHandler.rollbackTransaction();
    }

    @Override
    public boolean isTransactionOpen() {
        return boltStateHandler.isTransactionOpen();
    }

    @Override
    @Nonnull
    public Object setParameter(@Nonnull String name, @Nonnull String valueString) throws CommandException {
        try {
            String parameterName = CypherVariablesFormatter.unescapedCypherVariable(name);
            Object value = evaluator.evaluate(valueString, Object.class);
            queryParams.put(parameterName, new ParamValue(valueString, value));
            return value;
        } catch (EvaluationException e) {
           throw new CommandException(e.getMessage(), e);
        }
    }

    @Override
    @Nonnull
    public Map<String, Object> allParameterValues() {
        return queryParams.entrySet()
                .stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        value -> value.getValue().getValue()));
    }

    @Nonnull
    @Override
    public Map<String, ParamValue> getAllAsUserInput() {
        return queryParams;
    }

    void setCommandHelper(@Nonnull CommandHelper commandHelper) {
        this.commandHelper = commandHelper;
    }

    @Override
    public void reset() {
        boltStateHandler.reset();
    }

    protected void addRuntimeHookToResetShell() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::reset));
    }

}
