package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "create",
        description = "Create a new profile",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileCreateCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name"
    )
    private String name;

    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @Override
    public void run() {
        boolean verbose = parent.verbose();
        try{
            ProfileHandler.getInstance().createProfile(name);
            System.out.printf("created profile '%s'\n", name);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileCreateCommand()).execute(args);
    }
}
