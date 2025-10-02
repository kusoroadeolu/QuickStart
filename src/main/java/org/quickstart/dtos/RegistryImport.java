package org.quickstart.dtos;

import java.io.Serializable;
import java.util.List;

public record RegistryImport(List<String> existingServices){

    public String toString(){
        StringBuilder sb = new StringBuilder();
        existingServices.forEach(existingService -> sb.append(existingService).append("\n"));
        return sb.toString();
    }


}
