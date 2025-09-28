package org.quickstart.dtos;

import java.util.Map;

public record RegistryExport(Map<String, Object> composeMap, String absentServices) {

}

