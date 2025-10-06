package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "up",
        description = "Export profile content to a file",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileExportCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name to export"
    )
    private String profileName;


    @CommandLine.Option(
            names = {"-f", "--file"},
            description = "Output file name (in current directory)"
    )
    private String fileName;


    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @Override
    public void run() {
        boolean verbose = parent.verbose();
        try{
            ProfileHandler.getInstance().exportFromProfile(profileName, fileName);
            System.out.printf("exported profile '%s' to '%s'\n", profileName, fileName);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileExportCommand()).execute(args);
    }
}