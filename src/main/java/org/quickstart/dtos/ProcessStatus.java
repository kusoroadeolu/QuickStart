package org.quickstart.dtos;

public record ProcessStatus(
        int exitCode,
        String processStatus
) {

}
