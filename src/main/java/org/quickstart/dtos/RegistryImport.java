package org.quickstart.dtos;

import java.io.Serializable;
import java.util.List;

public record RegistryImport(List<String> existingServices){

    public String toString(){
        if(existingServices == null || existingServices.isEmpty()) {
            return "all services imported successfully";
        }

        int count = existingServices.size();
        StringBuilder sb = new StringBuilder(
                String.format("skipped %d existing service%s: ", count, count == 1 ? "" : "s")
        );
        sb.append(String.join(", ", existingServices));
        return sb.toString();
    }


}
