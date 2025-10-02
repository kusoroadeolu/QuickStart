package org.quickstart.dtos;

import java.util.Map;

public record ServiceExport(Map<String, Object> composeMap, String absentServices) {

}

