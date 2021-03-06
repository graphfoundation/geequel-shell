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
package org.neo4j.shell.cli;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.impl.action.StoreConstArgumentAction;
import net.sourceforge.argparse4j.impl.action.StoreTrueArgumentAction;
import net.sourceforge.argparse4j.impl.choice.CollectionArgumentChoice;
import net.sourceforge.argparse4j.impl.type.BooleanArgumentType;
import net.sourceforge.argparse4j.inf.*;

import java.io.PrintWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.String.format;
import static org.neo4j.shell.cli.FailBehavior.FAIL_AT_END;
import static org.neo4j.shell.cli.FailBehavior.FAIL_FAST;

/**
 * Command line argument parsing and related stuff
 */
public class CliArgHelper {

    static final Pattern ADDRESS_ARG_PATTERN =
            Pattern.compile("\\s*(?<scheme>[a-zA-Z0-9+\\-.]+://)?((?<username>\\w+):(?<password>[^\\s]+)@)?(?<host>[a-zA-Z\\d\\-.]+)?(:(?<port>\\d+))?\\s*");

    /**
     * @param args to parse
     * @return null in case of error, commandline arguments otherwise
     */
    @Nullable
    public static CliArgs parse(@Nonnull String... args) {
        final ArgumentParser parser = setupParser();
        final Namespace ns;

        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            return null;
        }

        // Parse address string, returns null on error
        final Matcher addressMatcher = parseAddressMatcher(parser, ns.getString("address"));

        if (addressMatcher == null) {
            return null;
        }

        CliArgs cliArgs = new CliArgs();

        cliArgs.setScheme(addressMatcher.group("scheme"), "bolt://");
        cliArgs.setHost(addressMatcher.group("host"), "localhost");
        // Safe, regex only matches integers
        String portString = addressMatcher.group("port");
        cliArgs.setPort(portString == null ? 7687 : Integer.parseInt(portString));
        // Also parse username and password from address if available
        cliArgs.setUsername(addressMatcher.group("username"), "");
        cliArgs.setPassword(addressMatcher.group("password"), "");

        // Only overwrite user/pass from address string if the arguments were specified
        String user = ns.getString("username");
        if (!user.isEmpty()) {
            cliArgs.setUsername(user, cliArgs.getUsername());
        }
        String pass = ns.getString("password");
        if (!pass.isEmpty()) {
            cliArgs.setPassword(pass, cliArgs.getPassword());
        }

        // Other arguments
        // geequel string might not be given, represented by null
        cliArgs.setCypher(ns.getString("geequel"));
        // Fail behavior as sensible default and returns a proper type
        cliArgs.setFailBehavior(ns.get("fail-behavior"));

        //Set Output format
        cliArgs.setFormat(Format.parse(ns.get("format")));

        cliArgs.setEncryption(ns.getBoolean("encryption"));

        cliArgs.setDebugMode(ns.getBoolean("debug"));

        cliArgs.setNonInteractive(ns.getBoolean("force-non-interactive"));

        cliArgs.setWrap(ns.getBoolean("wrap"));

        cliArgs.setNumSampleRows(ns.getInt("sample-rows"));

        cliArgs.setVersion(ns.getBoolean("version"));

        cliArgs.setDriverVersion(ns.getBoolean("driver-version"));

        return cliArgs;
    }

    @Nullable
    private static Matcher parseAddressMatcher(ArgumentParser parser, String address) {
        Matcher matcher = ADDRESS_ARG_PATTERN.matcher(address);
        if (!matcher.matches()) {
            // Match behavior in built-in error handling
            PrintWriter printWriter = new PrintWriter(System.err);
            parser.printUsage(printWriter);
            printWriter.println("geequel-shell: error: Failed to parse address: '" + address + "'");
            printWriter.println("\n  Address should be of the form: [scheme://][username:password@][host][:port]");
            printWriter.flush();
            return null;
        }
        return matcher;
    }

    private static ArgumentParser setupParser()
    {
        ArgumentParser parser = ArgumentParsers.newArgumentParser( "geequel-shell" ).defaultHelp( true ).description(
                format( "A command line shell where you can execute Geequel against an instance of ONgDB. " +
                        "By default the shell is interactive but you can use it for scripting by passing Geequel " +
                        "directly on the command line or by piping a file with Geequel statements (requires Powershell on Windows)." +
                        "%n%n" +
                        "example of piping a file:%n" +
                        "  cat some-geequel.txt | geequel-shell" ) );

        ArgumentGroup connGroup = parser.addArgumentGroup("connection arguments");
        connGroup.addArgument("-a", "--address")
                .help("address and port to connect to")
                .setDefault("bolt://localhost:7687");
        connGroup.addArgument("-u", "--username")
                .setDefault("")
                .help("username to connect as. Can also be specified using environment variable ONGDB_USERNAME");
        connGroup.addArgument("-p", "--password")
                .setDefault("")
                .help("password to connect with. Can also be specified using environment variable ONGDB_PASSWORD");
        connGroup.addArgument("--encryption")
                .help("whether the connection to ONgDB should be encrypted; must be consistent with ONgDB's " +
                        "configuration")
                .type(new BooleanArgumentType())
                .setDefault(true);

        MutuallyExclusiveGroup failGroup = parser.addMutuallyExclusiveGroup();
        failGroup.addArgument("--fail-fast")
                .help("exit and report failure on first error when reading from file (this is the default behavior)")
                .dest("fail-behavior")
                .setConst(FAIL_FAST)
                .action(new StoreConstArgumentAction());
        failGroup.addArgument("--fail-at-end")
                .help("exit and report failures at end of input when reading from file")
                .dest("fail-behavior")
                .setConst(FAIL_AT_END)
                .action(new StoreConstArgumentAction());
        parser.setDefault("fail-behavior", FAIL_FAST);

        parser.addArgument("--format")
                .help("desired output format, verbose displays results in tabular format and prints statistics, " +
                        "plain displays data with minimal formatting")
                .choices(new CollectionArgumentChoice<>(
                        Format.AUTO.name().toLowerCase(),
                        Format.VERBOSE.name().toLowerCase(),
                        Format.PLAIN.name().toLowerCase()))
                .setDefault(Format.AUTO.name().toLowerCase());

        parser.addArgument("--debug")
                .help("print additional debug information")
                .action(new StoreTrueArgumentAction());

        parser.addArgument("--non-interactive")
                .help("force non-interactive mode, only useful if auto-detection fails (like on Windows)")
                .dest("force-non-interactive")
              .action(new StoreTrueArgumentAction());

        parser.addArgument("--sample-rows")
                .help("number of rows sampled to compute table widths (only for format=VERBOSE)")
                .type(new PositiveIntegerType())
                .dest("sample-rows")
                .setDefault(CliArgs.DEFAULT_NUM_SAMPLE_ROWS);

        parser.addArgument("--wrap")
                .help("wrap table column values if column is too narrow (only for format=VERBOSE)")
                .type(new BooleanArgumentType())
                .setDefault(true);

        parser.addArgument("-v", "--version")
                .help("print version of geequel-shell and exit")
                .action(new StoreTrueArgumentAction());

        parser.addArgument("--driver-version")
                .help("print version of the ONgDB Driver used and exit")
                .dest("driver-version")
                .action(new StoreTrueArgumentAction());

        parser.addArgument("geequel")
                .nargs("?")
                .help("an optional string of geequel to execute and then exit");

        return parser;
    }

    private static class PositiveIntegerType implements ArgumentType<Integer> {
        @Override
        public Integer convert(ArgumentParser parser, Argument arg, String value) throws ArgumentParserException {
            try {
                int result = Integer.parseInt(value);
                if (result < 1) throw new NumberFormatException(value);
                return result;
            } catch (NumberFormatException nfe) {
                throw new ArgumentParserException("Invalid value: "+value, parser);
            }
        }
    }
}
