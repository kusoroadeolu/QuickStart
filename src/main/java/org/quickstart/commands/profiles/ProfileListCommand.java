package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "ls",
        description = "List all profiles",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileListCommand implements Runnable {

    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @Override
    public void run() {
        boolean verbose = parent.verbose();
        try{
            String str = ProfileHandler.getInstance().listAllProfiles();
            System.out.print(str);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileListCommand()).execute(args);
    }
}