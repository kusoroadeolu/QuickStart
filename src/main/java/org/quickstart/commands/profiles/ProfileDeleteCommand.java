package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.dtos.ProfileDeleteResult;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;

@CommandLine.Command(
        name = "rm",
        description = "Delete a profile",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
public final class ProfileDeleteCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name to delete"
    )
    private String profileName;

    @CommandLine.Option(
            names = "--all",
            description = "Delete all profiles"
    )
    boolean all = false;

    @CommandLine.ParentCommand
    public ProfileCommand command;

    @Override
    public void run() {
        boolean verbose = command.verbose();
        try{
            if(!all){
                ProfileHandler.getInstance().deleteProfile(profileName);
                System.out.printf("deleted profile '%s'\n", profileName);
            }else {
                ProfileDeleteResult result = ProfileHandler.getInstance().deleteAllProfiles();
                System.out.println(result);
            }

        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }


    public static void main(String[] args) throws Exception {
        new CommandLine(new ProfileDeleteCommand()).execute(args);
    }
}