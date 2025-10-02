package org.quickstart.configurations;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class ObjectMapperConfig {

    public static YAMLMapper getYAMLMapper() {
        return ObjectMapperHolder.YAML_MAPPER;
    }

    public static ObjectMapper getJsonMapper() {
        return ObjectMapperHolder.JSON_MAPPER;
    }

    protected static class ObjectMapperHolder{
        private final static ObjectMapper JSON_MAPPER = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .enable(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION.mappedFeature());
        private final static YAMLMapper YAML_MAPPER = new YAMLMapper(configureYamlFactory());
    }

    private static YAMLFactory configureYamlFactory(){
        return YAMLFactory.builder()
                .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)//Ensure arrays are always indented
                .enable(YAMLGenerator.Feature.LITERAL_BLOCK_STYLE)
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER) // Idk what this does honestly
                .disable(YAMLGenerator.Feature.USE_NATIVE_TYPE_ID) //This too
                .disable(YAMLGenerator.Feature.MINIMIZE_QUOTES) //Disable quote minimization
                .build();
    }
}
