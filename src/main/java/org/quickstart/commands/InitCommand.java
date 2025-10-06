package org.quickstart.commands;

import org.quickstart.exceptions.QuickStartException;
import org.quickstart.exceptions.ServiceError;
import org.quickstart.registry.QuickStartInitializer;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;
@Command(
        name = "init",
        description = "Initialize quickstart directory and registry",
        version = "1.0.0",
        mixinStandardHelpOptions = true
)
final class InitCommand implements Runnable {


    @ParentCommand
    private QuickStartCommand quickStartCommand;


    @Override
    public void run() {
        boolean verbose = quickStartCommand.verbose;
        try{
            QuickStartInitializer.initQuickStart();
            System.out.println("initialized quickstart at ~/.quickstart/");
        }catch(QuickStartException e){
            ServiceError error = e.serviceError();
            System.err.println(error.toString(verbose));
        }
    }

    public static void main(String[] args) throws Exception {
        new CommandLine(new InitCommand()).execute(args);
    }
}