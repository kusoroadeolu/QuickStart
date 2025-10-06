package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;

@CommandLine.Command(
        name = "exist",
        description = "Check if a profile exists",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
public final class ProfileExistCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name to check"
    )
    private String profileName;


    @CommandLine.ParentCommand
    public ProfileCommand command;

    @Override
    public void run() {
        boolean verbose = command.verbose();
        try{
            boolean exists = ProfileHandler.getInstance().doesProfileExist(profileName);

            if(exists){
                System.out.printf("profile '%s' exists\n", profileName);
            }else {
                System.out.printf("profile '%s' not found\n", profileName);
            }

        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }


    public static void main(String[] args) throws Exception {
        new CommandLine(new ProfileExistCommand()).execute(args);
    }
}