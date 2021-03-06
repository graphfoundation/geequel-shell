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
package org.neo4j.shell.log;

import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;
import org.neo4j.driver.v1.exceptions.ClientException;
import org.neo4j.shell.cli.Format;
import org.neo4j.shell.exception.AnsiFormattedException;

import javax.annotation.Nonnull;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.fusesource.jansi.internal.CLibrary.STDERR_FILENO;
import static org.fusesource.jansi.internal.CLibrary.STDOUT_FILENO;
import static org.fusesource.jansi.internal.CLibrary.isatty;

/**
 * A basic logger which prints Ansi formatted text to STDOUT and STDERR
 */
public class AnsiLogger implements Logger {
    private final PrintStream out;
    private final PrintStream err;
    private final boolean debug;
    private Format format;

    public AnsiLogger(final boolean debug) {
        this(debug, Format.VERBOSE, System.out, System.err);
    }

    public AnsiLogger(final boolean debug, @Nonnull Format format,
                      @Nonnull PrintStream out, @Nonnull PrintStream err) {
        this.debug = debug;
        this.format = format;
        this.out = out;
        this.err = err;

        try {
            if (isOutputInteractive()) {
                Ansi.setEnabled(true);
                AnsiConsole.systemInstall();
            } else {
                Ansi.setEnabled(false);
            }
        } catch (Throwable t) {
            // Not running on a distro with standard c library, disable Ansi.
            Ansi.setEnabled(false);
        }
    }

    @Nonnull
    private static Throwable getRootCause(@Nonnull final Throwable th) {
        Throwable cause = th;
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * @return true if the shell is outputting to a TTY, false otherwise (e.g., we are writing to a file)
     * @throws UnsatisfiedLinkError maybe if standard c library can't be found
     * @throws NoClassDefFoundError maybe if standard c library can't be found
     */
    private static boolean isOutputInteractive() {
        return 1 == isatty(STDOUT_FILENO) && 1 == isatty(STDERR_FILENO);
    }

    @Nonnull
    @Override
    public PrintStream getOutputStream() {
        return out;
    }

    @Nonnull
    @Override
    public PrintStream getErrorStream() {
        return err;
    }

    @Nonnull
    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public void setFormat(@Nonnull Format format) {
        this.format = format;
    }

    @Override
    public boolean isDebugEnabled() {
        return debug;
    }

    @Override
    public void printError(@Nonnull Throwable throwable) {
        printError(getFormattedMessage(throwable));
    }

    @Override
    public void printError(@Nonnull String s) {
        err.println(Ansi.ansi().render(s).toString());
    }

    @Override
    public void printOut(@Nonnull final String msg) {
        out.println(Ansi.ansi().render(msg).toString());
    }

    /**
     * Interpret the cause of a Bolt exception and translate it into a sensible error message.
     */
    @Nonnull
    String getFormattedMessage(@Nonnull final Throwable e) {
        AnsiFormattedText msg = AnsiFormattedText.s().colorRed();

        if (isDebugEnabled()) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos);
            e.printStackTrace(ps);
            msg.append(new String(baos.toByteArray(), StandardCharsets.UTF_8));
        } else {
            //noinspection ThrowableResultOfMethodCallIgnored
            final Throwable cause = getRootCause(e);

            if (cause instanceof AnsiFormattedException) {
                msg = msg.append(((AnsiFormattedException) cause).getFormattedMessage());
            } else if (cause instanceof ClientException &&
                    cause.getMessage() != null && cause.getMessage().contains("Missing username")) {
                // Username and password was not specified
                msg = msg.append(cause.getMessage())
                         .append("\nPlease specify --username, and optionally --password, as argument(s)")
                         .append("\nor as environment variable(s), ONGDB_USERNAME, and ONGDB_PASSWORD respectively.")
                         .append("\nSee --help for more info.");
            } else {
                if (cause.getMessage() != null) {
                    msg = msg.append(cause.getMessage());
                } else {
                    msg = msg.append(cause.getClass().getSimpleName());
                }
            }
        }

        return msg.formattedString();
    }
}
