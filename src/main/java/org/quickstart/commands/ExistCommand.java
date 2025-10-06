package org.quickstart.commands;

import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

@CommandLine.Command(
        name = "exist",
        description = "Checks if a service exists ",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
public final class ExistCommand implements Runnable {

    @CommandLine.Parameters(
            description = "The name of the service to check if exists"
    )
    private String serviceName;


    @CommandLine.ParentCommand
    public QuickStartCommand command;

    @Override
    public void run() {
        boolean verbose = command.verbose;
        try{
            boolean exists = RegistryHandler.getInstance().doesServiceExist(serviceName);

            if(!exists){
                System.out.printf("%s does not exist", serviceName);
            }else {
                System.out.printf("%s exists", serviceName);
            }

        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }


    public static void main(String[] args) throws Exception {
        new CommandLine(new org.quickstart.commands.profiles.ProfileExistCommand()).execute(args);
    }
}
