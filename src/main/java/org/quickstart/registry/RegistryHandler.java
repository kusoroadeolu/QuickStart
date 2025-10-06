package org.quickstart.registry;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.quickstart.compose.ComposeBuilder;
import org.quickstart.compose.TempComposeFile;
import org.quickstart.configurations.ObjectMapperConfig;
import org.quickstart.dtos.RegistryExport;
import org.quickstart.dtos.RegistryImport;
import org.quickstart.dtos.ServiceExport;
import org.quickstart.exceptions.QuickStartException;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

import static org.quickstart.constants.QuickStartConstants.REGISTRY_PATH;
import static org.quickstart.constants.QuickStartConstants.USER_DIR;
import static org.quickstart.registry.RegistryIOUtils.readFromRegistry;
import static org.quickstart.registry.RegistryIOUtils.writeToRegistry;

public final class RegistryHandler {

    private final ObjectMapper jsonMapper;
    private final YAMLMapper yamlMapper;
    private final JsonNode registryRootNode;

    private RegistryHandler() throws RegistryException{
        this.jsonMapper = ObjectMapperConfig.getJsonMapper();
        this.yamlMapper = ObjectMapperConfig.getYAMLMapper();
        this.registryRootNode = registryRootNode();
    }

    /**
     * Write a map of services to the registry from the CLI (key -> service name, value -> service config)
     *
     * @param map {@link HashMap} Maps each service name to their config
     * @return a dto containing a list of services included by the user already existing in the registry
     * */
    // I personally won't suggest using this method, it's here for convenience for the wackos who will paste their
    // yaml config into the CLI
    public RegistryImport importToRegistryFromText(Map<String, String> map) throws RegistryException{
        if(map == null || map.isEmpty()) {
            throw new QuickStartException("Empty registry file");
        }
        List<String> existingServices = new ArrayList<>(); //A list of services which already exist in the registry

        //Map the yaml string to its key and a json node(its tree)
        Map<String, JsonNode> filtered  = map
                .entrySet()
                .stream()
                .filter(Objects::nonNull)
                .filter(k -> k.getValue() != null && !k.getValue().isEmpty())
                .collect(HashMap::new, (m, e) ->
                {
                    try {
                        if(registryRootNode.has(e.getKey())) {
                            existingServices.add(e.getValue());
                        }else{
                            m.put(e.getKey(), yamlMapper.readValue(e.getValue(), JsonNode.class));
                        }
                    } catch (JsonProcessingException ex) {
                        throw new RegistryException(
                                new ServiceError("invalid YAML syntax", "check your service configuration format", ex)
                        );
                    }
                }, HashMap::putAll);

        writeToRegistry(jsonMapper, filtered);
        return new RegistryImport(existingServices);
    }


    /**
     * Import services from a yaml file to a registry
     * @param fileName The name of the yaml file
     * @param excludedServices The services you want to exclude from the yaml file. Leave empty if you want to exclude nothing
     * @param force Overwrites already existing services
     * @return a dto containing a list of services included by the user already existing in the registry
     * */
    //My suggested method
    public RegistryImport importToRegistryFromYaml(String fileName, Set<String> excludedServices ,boolean force) throws RegistryException{
        Path path = Path.of(USER_DIR.toString(), fileName).normalize();
        validateFile(path);

        try{
            JsonNode rootNode = yamlMapper.readTree(path.toFile());

            if(rootNode == null || rootNode.isEmpty()){
                throw new RegistryException(
                        new ServiceError("empty YAML file", String.format("add some content to `%s`", path))
                );
            }

            //Get the services tree
            JsonNode servicesNode = rootNode.get("services");

            if(servicesNode == null || servicesNode.isEmpty()){
                throw new RegistryException(
                        new ServiceError("no 'services' section found", "add a `services:` section to your YAML file")
                );
            }

            //Map the yaml to key val map and convert to json
            Map<String, JsonNode> servicesMap = new HashMap<>();
            List<String> existingServices = new ArrayList<>();
            Set<Map.Entry<String, JsonNode>> servicesSet = servicesNode.properties();


            if(force){
                servicesSet.forEach(e -> {
                    if(!excludedServices.contains(e.getKey())){
                        servicesMap.put(e.getKey(), e.getValue());
                    }

                });
            }else{
                servicesSet.forEach(e -> {
                    if(!excludedServices.contains(e.getKey()) && registryRootNode.has(e.getKey())) {
                        existingServices.add(e.getKey());
                    }else{
                        servicesMap.put(e.getKey(), e.getValue());
                    }
                });
            }

            writeToRegistry(jsonMapper, servicesMap);
            return new RegistryImport(existingServices);

        }catch(IOException e){
            throw new RegistryException(
                    new ServiceError(
                            String.format("cannot read file `%s`", path),
                            "check if the file exists and is readable"
                    )
            );
        }
    }

    /**
     * Export services from the registry as text
     * @param services The list of services given by the use
     * @return an export class containing a yaml string of all the services found, and a string of those not found in the registry
     * */
    public RegistryExport exportFromRegistryAsText(Set<String> services) throws RegistryException{
        if(services == null || services.isEmpty()){
            throw new RegistryException(
                    new ServiceError("no services specified", "provide at least one service name")
            );
        }

        @SuppressWarnings("unchecked")
        Map<String, JsonNode> registryServices = jsonMapper.convertValue(registryRootNode, Map.class);

        ServiceExport export =
                readFromRegistry(registryRootNode, registryServices  ,services);

        Map<String, Object> serviceMap = export.composeMap(); //Get the service map

        try{
            String yamlString = yamlMapper.writeValueAsString(serviceMap);
            return new RegistryExport(yamlString, export.absentServices());
        }catch(Exception e){
            throw new RegistryException(
                    new ServiceError(
                            "failed to generate YAML output",
                            "check service configurations for invalid syntax",
                            e)
            );
        }

    }

    /**
     * Export services from the registry into a temp yaml file, then runs it
     * @param services The list of services given by the use
     * @return an export class containing a yaml string of all the services found, and a string of those not found in the registry
     * */
    public RegistryExport buildFromRegistryAndRun(Set<String> services) throws RegistryException{
        RegistryExport export = exportFromRegistryAsText(services);

        try(TempComposeFile composeFile = new TempComposeFile(export.yamlString())){
            composeFile.runTempFile();
            return export;
        }catch (IOException e){
            throw new RegistryException(
                    new ServiceError(
                            "failed to create temp compose file",
                            "check disk space and /tmp directory permissions",
                            e)
            );
        }catch (InterruptedException e){
            throw new RegistryException(
                    new ServiceError(
                            "docker command was interrupted",
                            "try running the command again",
                            e)
            );
        }
    }


    /**
     * Export services from the registry to a yaml file
     * @param services The set of services given by the user
     * @param fileName The name of the file
     * @return an export class containing a yaml string of all the services found, and a string of those not found in the registry
     * */
    public RegistryExport exportFromRegistryToFile(Set<String> services, String fileName) throws RegistryException{
        Path userPath = Path.of(USER_DIR.toString(), fileName);
        validateFile(userPath);
        RegistryExport registryExport = exportFromRegistryAsText(services);
        try{
            Files.write(userPath, registryExport.yamlString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return registryExport;
        }catch(IOException e){
            throw new RegistryException(
                    new ServiceError(
                            String.format("cannot write to `%s`", userPath),
                            "check directory permissions and disk space"
                    )
            );
        }
    }

    /**
     * Deletes a set of services from the registry
     * @param servicesToDelete The set of services to delete
     * @return {@link RegistryExport}
     * */
    public RegistryExport deleteServicesFromRegistry(Set<String> servicesToDelete) throws RegistryException{
        if(servicesToDelete == null || servicesToDelete.isEmpty()){
            return new RegistryExport("", "");
        }

        @SuppressWarnings("unchecked")
        Map<String, JsonNode> registryMap = jsonMapper.convertValue(registryRootNode, Map.class);

        if(registryRootNode.isEmpty()){
            return new RegistryExport("", "no services found in the registry to delete");
        }

        try{
            ComposeBuilder builder = ComposeBuilder
                    .create()
                    .delete(registryMap, servicesToDelete)
                    .mapAbsentServicesToSimilarServices(registryMap.keySet());

            String modifiedRegistryString = jsonMapper.writeValueAsString(builder.presentServices());
            Files.writeString(REGISTRY_PATH, modifiedRegistryString);

            //We don't need to return a string of what was written to the json node,
            // but we need to return absent services (services not found) as a string
            return new RegistryExport("", builder.absentServicesToString());
        }catch (IOException e){
            throw new RegistryException(String.format("Failed to write registry to file %s.", registryMap));
        }
    }

    /**
     * Deletes all services from the registry
     * */
    public void deleteAllServicesFromTheRegistry() throws RegistryException {
        try{
            String jsonString = jsonMapper.writeValueAsString(new HashMap<>());
            Files.writeString(REGISTRY_PATH, jsonString, StandardOpenOption.TRUNCATE_EXISTING);
        }catch (IOException e){
            throw new RegistryException(
                    new ServiceError(
                            "cannot clear registry",
                            "check file permissions for ~/.quickstart/registry.yaml",
                            e)
            );
        }
    }

    //Checks if a service exists
    public boolean doesServiceExist(String serviceName){
        return registryRootNode.has(serviceName);
    }


    //List all services in the registry
    public String listAllServicesInRegistry(){
        List<String> services =
                registryRootNode.propertyStream().map(Map.Entry::getKey).toList();

        if(services.isEmpty()){
            return "no services in registry\nhint: use `qs add -f <file>` to import services";
        }

        StringBuilder sb = new StringBuilder(String.format("found %d service%s:\n",
                services.size(),
                services.size() == 1 ? "" : "s"));

        for(int i = 0; i < services.size(); i++) {
            sb.append(String.format("  %d. %s\n", i + 1, services.get(i)));
        }

        return sb.toString().trim();
    }

    //Get registry root node
    private JsonNode registryRootNode() throws RegistryException{
        try{
            return jsonMapper.readTree(REGISTRY_PATH.toFile());
        }catch(IOException e){
            throw new RegistryException(
                    new ServiceError("cannot read registry file",
                            "run `quickstart init` to create it or ensure the registry isn't corrupted", e)
            );
        }
    }


    private void validateFile(Path userPath) throws RegistryException{
        if(!Files.exists(userPath)){
            throw new RegistryException(
                    new ServiceError(
                            String.format("file `%s` not found", userPath),
                            "check the file path and try again"
                    )
            );
        }
    }


    public static RegistryHandler getInstance() throws RegistryException{
        return QuickStartClassHolder.INSTANCE;
    }



    private static class QuickStartClassHolder  {
        private static final RegistryHandler INSTANCE = new RegistryHandler();
    }
}
