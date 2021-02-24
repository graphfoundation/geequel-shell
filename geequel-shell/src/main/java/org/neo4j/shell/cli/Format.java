package org.neo4j.shell.cli;

import javax.annotation.Nonnull;

import static org.neo4j.shell.ShellRunner.isInputInteractive;
import static org.neo4j.shell.ShellRunner.isOutputInteractive;

public enum Format {
    // Will select depending based on STDOUT and STDIN redirection
    AUTO,
    // Intended for human consumption
    VERBOSE,
    // Intended for machine consumption (nothing except data is printed
    PLAIN;
    // TODO JSON, strictly intended for machine consumption with data formatted in JSON

    public static Format parse(@Nonnull String format) {
        if (format.equalsIgnoreCase(PLAIN.name())) {
            return PLAIN;
        } else if (format.equalsIgnoreCase( VERBOSE.name() )) {
            return VERBOSE;
        } else {
            return isInputInteractive() && isOutputInteractive() ? VERBOSE : PLAIN;
        }
    }
}
