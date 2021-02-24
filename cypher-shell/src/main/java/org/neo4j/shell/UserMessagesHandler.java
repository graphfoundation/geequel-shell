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
