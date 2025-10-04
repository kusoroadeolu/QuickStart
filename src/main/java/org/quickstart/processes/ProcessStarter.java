package org.quickstart.processes;

import java.io.IOException;

public final class ProcessStarter {
    private final static ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();

    private ProcessStarter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    /**
     * This method starts a process, ideally a docker process in this case
     * @param args The arguments the process builder should execute
     *
     * */
    public static void startProcess(String... args) throws IllegalArgumentException, IOException, InterruptedException {
        if(args.length == 0){
            throw new IllegalArgumentException();
        }

        Process process = PROCESS_BUILDER
                    .command(args)
                    .inheritIO()
                    .start();

        process.waitFor(); //Wait for the process to completely run


    }

}