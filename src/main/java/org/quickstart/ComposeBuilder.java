package org.quickstart;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


public class ComposeBuilder {

    /**
     * Maps service names found in the input to their corresponding JSON nodes.
     */
    private final Map<String, JsonNode> presentServices;
    /**
     * Maps an unfound service name to what the user potentially meant (a similar service name).
     */
    private final Map<String, String> absentServices;
    private final static LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();
    private final static int MAX_SERVICE_SIMILARITY_COUNT = 3;

    private ComposeBuilder() {
        this.absentServices = new HashMap<>();
        this.presentServices = new HashMap<>();
    }

    /**
     * Creates a new instance of ComposeBuilder.
     *
     * @return A new ComposeBuilder instance.
     */
    public static ComposeBuilder create() {
        return new ComposeBuilder();
    }

    /**
     * Builds the compose builder by populating present and absent services based on a root node and expected services.
     *
     * @param rootNode The root JSON node containing available services.
     * @param expectedServices The set of service names expected to be present.
     * @return The current ComposeBuilder instance.
     * @throws IOException If an I/O error occurs (though not explicitly thrown by this method's body, kept for signature).
     */
    public ComposeBuilder build(JsonNode rootNode, Set<String> expectedServices) throws IOException {
        if (rootNode.isEmpty()) {
            return null;
        }

        for (String service : expectedServices) {
            if (!rootNode.has(service)) {
                absentServices.put(service, "");
            }else{
                presentServices.put(service, rootNode.get(service));
            }
        }

        return this;
    }

    /**
     * Adds services from a registry and deletes a specified set of services.
     *
     * @param registryServices A map of existing services to add to presentServices.
     * @param servicesToDelete The set of service names to delete.
     * @return The current ComposeBuilder instance.
     */
    public ComposeBuilder delete(Map<String, JsonNode> registryServices, Set<String> servicesToDelete) {
        this.presentServices.putAll(registryServices);
        servicesToDelete.forEach((service) -> {
            if (!registryServices.containsKey(service)) {
                absentServices.put(service, "");
            }else {
                presentServices.remove(service);
            }
        });
        return this;
    }

    /**
     * Maps each absent service to the most similar service string in the registry
     * based on Levenshtein distance (distance <= 1).
     *
     * @param registryServices The set of all available service names in the registry.
     * @return The current ComposeBuilder instance.
     */
    public ComposeBuilder mapAbsentServicesToSimilarServices(Set<String> registryServices) {
        int dist; //Similarity with the service

        for (String absentService : absentServices.keySet()) {
            StringBuilder similarServices = new StringBuilder();
            int count = 0;
            for(String service : registryServices) {
                dist = levenshteinDistance.apply(service, absentService);

                if(dist <= 1){
                    similarServices.append(service).append(" ");
                    count++;
                }

                if (count >= MAX_SERVICE_SIMILARITY_COUNT) {
                    break;
                }
            }

            absentServices.put(absentService, similarServices.toString().trim());
        }
        return this;
    }


    /**
     * Generates a detailed string report of all absent services and their potential matches.
     *
     * @return A string detailing absent services.
     */
    public String absentServicesToString(){
        StringBuilder sb = new StringBuilder();
        List<String> list  = new ArrayList<>(); //List to store keys where we couldn't find similar keys for them in the reg
        absentServices.forEach((key, value) -> {
            if(value.isEmpty()){
                list.add(key);
            }
            sb.append(key).append(" -> ").append(value).append(" \n");
        });

        if(!list.isEmpty()){
            sb.append("We couldn't find similar matches for these keys in the registry: ");
            list.forEach(s -> sb.append(s.trim()).append("\n"));
        }

        return sb.toString();
    }

    /**
     * Returns the map of services found in the input or registry.
     *
     * @return A map of present service names to their JSON nodes.
     */
    public Map<String, JsonNode> presentServices() {
        return presentServices;
    }

    /**
     * Returns the map of services not found, with potential matches as values.
     *
     * @return A map of absent service names to their similar service suggestions.
     */
    public Map<String, String> absentServices() {
        return absentServices;
    }


}