package org.quickstart.commands;

import org.quickstart.dtos.RegistryExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;

import java.util.HashSet;
import java.util.Set;

@CommandLine.Command(
        name = "export",
        description = "Export services to a YAML file",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
final class ExportCommand implements Runnable {

    @CommandLine.ParentCommand
    private QuickStartCommand quickStartCommand;

    @CommandLine.Parameters(
            description = "Services to export",
            arity = "1..*"
    )
    private Set<String> servicesToExport = new HashSet<>();

    @CommandLine.Option(
            names = {"-f", "--file"},
            description = "Output file name (in current directory)"
    )
    private String fileName;

    @Override
    public void run() {
        boolean verbose = quickStartCommand.verbose;
        try{
            RegistryExport export = RegistryHandler.getInstance().exportFromRegistryToFile(servicesToExport, fileName);
            System.out.println(export.toString());
            System.out.println(export.yamlString());
        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args) {
        new CommandLine(new ExportCommand()).execute(args);
    }
}