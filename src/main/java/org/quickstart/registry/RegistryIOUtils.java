package org.quickstart.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.quickstart.ComposeBuilder;
import org.quickstart.dtos.RegistryExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.quickstart.constants.QuickStartConstants.COMPOSE_VERSION;
import static org.quickstart.constants.QuickStartConstants.REGISTRY_PATH;

class RegistryIOUtils {

    protected static void writeToRegistry(ObjectMapper jsonMapper, Map<String, JsonNode> map) throws RegistryException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, JsonNode> fileMap = jsonMapper.readValue(REGISTRY_PATH.toFile(), Map.class);

            if (fileMap == null || fileMap.isEmpty()) {
                fileMap = new HashMap<>();
            }

            Map<String, JsonNode> mergedMap = new HashMap<>(map);
            mergedMap.putAll(fileMap);
            jsonMapper.writeValue(REGISTRY_PATH.toFile(), mergedMap);

        } catch (IOException e) {
            throw new RegistryException(
                    new ServiceError(
                            "cannot write to registry file",
                            "check if another process is using the file or run `ls -la ~/.quickstart/`",
                            e)
            );
        } catch (Exception e) {
            throw new RegistryException(
                    new ServiceError(
                            "registry file is corrupted",
                            "backup and delete `~/.quickstart/registry.yaml`, then run `quickstart init`",
                            e)
            );
        }
    }

    protected static RegistryExport readFromRegistry(JsonNode rootNode, Map<String, JsonNode> registryServices ,Set<String> services)
            throws RegistryException {
        try {

            ComposeBuilder builder = ComposeBuilder
                    .create()
                    .build(rootNode, services)
                    .mapAbsentServicesToSimilarServices(registryServices.keySet());

            Map<String, Object> composeMap = new LinkedHashMap<>();
            composeMap.put("version", COMPOSE_VERSION);
            composeMap.put("services", builder.presentServices());
            return new RegistryExport(composeMap, builder.absentServicesToString());

        } catch (IOException e) {
            throw new RegistryException(
                    new ServiceError(
                            "cannot read registry file",
                            "ensure `~/.quickstart/registry.yaml` exists and is readable",
                            e)
            );
        } catch (Exception e) {
            throw new RegistryException(
                    new ServiceError(
                            "invalid service configuration",
                            "check YAML syntax in your requested services",
                            e)
            );
        }
    }



}