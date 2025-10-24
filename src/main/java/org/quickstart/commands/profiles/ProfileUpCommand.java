package org.quickstart.commands.profiles;

import org.quickstart.commands.ProfileCommand;
import org.quickstart.dtos.ProfileDto;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.profiles.ProfileHandler;
import picocli.CommandLine;


@CommandLine.Command(
        name = "up",
        description = "Start all services in a profile",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
public final class ProfileUpCommand implements Runnable {

    @CommandLine.Parameters(
            description = "Profile name to start"
    )
    private String name;

    @CommandLine.ParentCommand
    private ProfileCommand parent;

    @Override
    public void run() {
        boolean verbose = parent.verbose();
        ProfileDto dto = null;
        try{
            dto = ProfileHandler.getInstance().runProfile(name);
            System.out.printf("started profile '%s'\n", name);
        }catch(ProfileException e){
            System.err.println(e.serviceError().toString(verbose));
            System.out.println(dto == null ? null : dto.toString());
        }
    }

    public static void main(String[] args)  {
        new CommandLine(new ProfileUpCommand()).execute(args);
    }
}