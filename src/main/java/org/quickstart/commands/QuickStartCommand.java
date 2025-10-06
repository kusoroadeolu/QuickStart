package org.quickstart.commands;

import picocli.CommandLine;

import static picocli.CommandLine.ScopeType.INHERIT;

@CommandLine.Command(
        version = "1.0",
        name = "qs",
        description = "Manage Docker Compose services and profiles",
        mixinStandardHelpOptions = true,
        subcommands = {
                InitCommand.class,
                AddCommand.class,
                RemoveCommand.class,
                UpCommand.class,
                ShowCommand.class,
                ListCommand.class,
                ProfileCommand.class,
                ExportCommand.class,
                ExistCommand.class
        }
)
final class QuickStartCommand implements Runnable {

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            description = "Show stack traces on errors",
            scope = INHERIT
    )
    public boolean verbose = false;

    @Override
    public void run() {
        // Show help when qs command is called without subcommands
        new CommandLine(this).usage(System.out);
    }

    public static void main(String[] args) throws Exception {
        new  CommandLine(new QuickStartCommand()).execute(args);
    }

}