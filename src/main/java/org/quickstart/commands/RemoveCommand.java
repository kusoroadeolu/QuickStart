package org.quickstart.commands;

import org.quickstart.dtos.RegistryExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.Set;

@CommandLine.Command(
        name = "rm",
        description = "Remove services from registry",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
final class RemoveCommand implements Runnable {

    @CommandLine.Parameters(
            arity = "1..*",
            description = "Services to remove"
    )
    private Set<String> servicesToRemove = new HashSet<>();

    @CommandLine.Option(
            names = "--all",
            description = "Remove all services from registry"
    )
    boolean all = false;

    @CommandLine.ParentCommand
    public QuickStartCommand command;

    @Override
    public void run() {
        boolean verbose = command.verbose;
        try{
            if(!all){
                RegistryExport export = RegistryHandler.getInstance().deleteServicesFromRegistry(servicesToRemove);
                System.out.println(export.toString(""));
            }else{
                RegistryHandler.getInstance().deleteAllServicesFromTheRegistry();
                System.out.println("removed all services from registry");
            }

        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args) throws Exception {
        new CommandLine(new RemoveCommand()).execute(args);
    }
}