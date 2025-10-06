package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "up",
        description = "Import file content to a profile",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileImportCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name to import to"
    )
    private String profileName;


    @CommandLine.Option(
            names = {"-f", "--file"},
            description = "Source file to import from (in current directory)"
    )
    private String fileName;


    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @Override
    public void run() {
        boolean verbose = parent.verbose();
        try{
            ProfileHandler.getInstance().importToProfile(profileName, fileName);
            System.out.printf("imported '%s' to profile '%s'\n", fileName, profileName);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileImportCommand()).execute(args);
    }
}