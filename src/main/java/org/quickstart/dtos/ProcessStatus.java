package org.quickstart.dtos;

public class ProcessStatus {
    private int exitCode;
    private String output;

    public ProcessStatus(int exitCode, String output) {
        this.exitCode = exitCode;
        this.output = output;
    }

    public int getExitCode() {
        return exitCode;
    }

    public String getOutput() {
        return output;
    }
}
