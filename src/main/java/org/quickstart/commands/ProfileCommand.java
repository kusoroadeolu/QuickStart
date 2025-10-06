package org.quickstart.commands;

import org.quickstart.commands.profiles.*;
import picocli.CommandLine;

@CommandLine.Command(
        name = "profile",
        description = "Manage profiles",
        mixinStandardHelpOptions = true,
        version = "1.0",
        subcommands = {
                ProfileCreateCommand.class,
                ProfileListCommand.class,
                ProfileUpCommand.class,
                ProfileDeleteCommand.class,
                ProfileExportCommand.class,
                ProfileImportCommand.class,
                ProfileShowCommand.class,
                ProfileExistCommand.class,
        }
)
public final class ProfileCommand implements Runnable {

    @CommandLine.ParentCommand
    private QuickStartCommand parent;

    public boolean verbose() {
        return parent.verbose;
    }

    @Override
    public void run() {
        // Show help when profile command is called without subcommands
        new CommandLine(this).usage(System.out);
    }

    public static void main(final String[] args) {
        new CommandLine(new ProfileCommand()).execute(args);
    }

}