package org.quickstart.commands;

import org.quickstart.dtos.RegistryExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.Set;

@CommandLine.Command(
        name = "show",
        version = "1.0.0",
        description = "Display service configurations as YAML",
        mixinStandardHelpOptions = true

)
final class ShowCommand implements Runnable {

    @CommandLine.ParentCommand
    private QuickStartCommand quickStartCommand;

    @CommandLine.Parameters(
            description = "Services to display"
    )
    private Set<String> servicesToExport = new HashSet<>();

    @Override
    public void run() {
        boolean verbose = quickStartCommand.verbose;

        try{
            RegistryExport export = RegistryHandler.getInstance().exportFromRegistryAsText(servicesToExport);
            System.out.println(export.toString(""));
            System.out.println(export.yamlString());
        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }


    public static void main(String[] args) throws Exception {
        new CommandLine(new ShowCommand()).execute(args);
    }
}