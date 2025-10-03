package org.quickstart.processes;

import java.io.IOException;

public class ProcessStarter {
    private final static ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();

    /**
     * This method starts a process, ideally a docker process in this case
     * @param args The arguments the process builder should execute
     *
     * */
    public void startProcess(String... args) throws IllegalArgumentException {
        if(args.length == 0){
            throw new IllegalArgumentException();
        }

        try {
            Process process = PROCESS_BUILDER
                    .command(args)
                    .redirectErrorStream(true)
                    .inheritIO()
                    .start();

            process.waitFor(); //Wait for the process to completely run

        } catch (IOException | InterruptedException e ) {
            throw new IllegalArgumentException(e);
        }

    }

}