package org.quickstart.dtos;

public record RegistryExport(
        String yamlString,
        String absentServices
) {
}
