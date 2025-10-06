package org.quickstart.commands;

import org.quickstart.dtos.RegistryImport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.registry.RegistryHandler;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.ParentCommand;

import java.util.HashSet;
import java.util.Set;

@Command(
        name = "add",
        description = "Add services from a YAML file to registry",
        mixinStandardHelpOptions = true,
        version = "1.0"
)
final class AddCommand implements Runnable {

    @Option(
            names = {"--force"},
            description = "Overwrite existing services in registry"
    )
    private boolean force = false;

    @Option(
            names = {"-e", "--exclude"},
            description = "Services to exclude from import"
    )
    private Set<String> excludedServices = new HashSet<>();

    @Option(
            names = {"-f", "--file"},
            description = "YAML file to import from",
            required = true
    )
    private String fileName;

    @ParentCommand
    private QuickStartCommand quickStartCommand;

    @Override
    public void run() {
        boolean verbose = quickStartCommand.verbose;
        try{
            RegistryImport imported = RegistryHandler.getInstance().importToRegistryFromYaml(fileName, excludedServices, force);
            System.out.println(imported);
        }catch(RegistryException e){
            System.err.println(e.serviceError().toString(verbose));
        }
    }

    public static void main(String[] args) {
        new CommandLine(new AddCommand()).execute(args);
    }
}