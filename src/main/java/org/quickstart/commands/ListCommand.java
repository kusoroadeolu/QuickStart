package org.quickstart.commands;

import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

@CommandLine.Command(
        name = "ls",
        version = "1.0.0",
        description = "List all services in registry",
        mixinStandardHelpOptions = true

)
final class ListCommand implements Runnable {

    @CommandLine.ParentCommand
    private QuickStartCommand command;

    public static void  main(String[] args) {
        new CommandLine(new ListCommand()).execute(args);
    }


    @Override
    public void run() {
        boolean verbose = command.verbose;
        try{
            String str = RegistryHandler.getInstance().listAllServicesInRegistry();
            System.out.println(str);
        }catch (RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

}