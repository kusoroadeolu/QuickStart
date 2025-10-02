package org.quickstart;

import java.io.*;

public class ProcessStarter {

    private final static ProcessBuilder PROCESS_BUILDER = new ProcessBuilder();

    public void startProcess(String... args){
        if(args.length == 0){
            throw new IllegalArgumentException("Please specify command line arguments"); //Docker cmds are empty??
        }

        try {
            Process process = PROCESS_BUILDER.command(args).redirectErrorStream(true).start();

            try (InputStream inputStream = process.getInputStream()) {
                    BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(inputStream));
                    inputStreamReader
                            .lines()
                            .forEach(IO::println);
            } catch (IOException ex) {
                InputStream errorStream = process.getErrorStream();
                BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(errorStream));
                errorStreamReader.lines().forEach(IO::println);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid command line arguments");
        }
    }

}