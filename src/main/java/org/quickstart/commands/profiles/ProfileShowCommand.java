package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "show",
        description = "Display profile content",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileShowCommand implements Runnable {

    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @CommandLine.Parameters(
            description = "Profile name to display"
    )
    private String profileName;

    @Override
    public void run() {
        boolean verbose = parent.verbose();

        try{
            String str = ProfileHandler.getInstance().exportProfileContentAsText(profileName);
            System.out.print(str);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileShowCommand()).execute(args);
    }
}