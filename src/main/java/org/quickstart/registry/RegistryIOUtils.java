package org.quickstart.registry;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.quickstart.ComposeBuilder;
import org.quickstart.dtos.ServiceExport;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

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

    protected static ServiceExport readFromRegistry(JsonNode rootNode, Map<String, JsonNode> registryServices , Set<String> services)
            throws RegistryException {
        try {

            ComposeBuilder builder = ComposeBuilder
                    .create()
                    .buildServices(rootNode, services)
                    .mapAbsentServicesToSimilarServices(registryServices.keySet());

            Map<String, Object> composeMap = new LinkedHashMap<>();
            composeMap.put("services", builder.presentServices());
            composeMap.put("volumes", builder.serviceVolumes());
            return new ServiceExport(composeMap, builder.absentServicesToString());

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