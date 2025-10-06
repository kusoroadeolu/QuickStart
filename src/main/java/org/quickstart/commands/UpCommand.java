package org.quickstart.commands;

import org.quickstart.dtos.RegistryExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.Set;

import static picocli.CommandLine.Command;

@Command(
        name = "up",
        description = "Start services from registry",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
final class UpCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Services to start",
            arity = "1..*"
    )
    private Set<String> services = new HashSet<>();

    @CommandLine.ParentCommand
    private QuickStartCommand quickStartCommand;

    @Override
    public void run() {
        boolean verbose = quickStartCommand.verbose;

        try{
            RegistryExport ex = RegistryHandler.getInstance().buildFromRegistryAndRun(services);
            System.out.println(ex.toString("started services"));
        }catch(RegistryException e){
            ServiceError error = e.serviceError();
            System.err.println(error.toString(verbose));
        }
    }

    public static void  main(String[] args) throws Exception {
        new CommandLine(new UpCommand()).execute(args);
    }
}