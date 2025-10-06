package org.quickstart.dtos;

public record RegistryExport(
        String yamlString,
        String absentServices
) {

    public String toString(String onSuccess){
        if(!absentServices.isEmpty()){
            return absentServices;
        }else{
            return onSuccess;
        }
    }

}
