package org.quickstart.dtos;

import java.util.List;
import java.util.Set;

/**
 * Just a simple dto to store a profile which wasn't found and profile names similar to it
 * */
public record ProfileDto(
        String profileName,
        Set<String> similarProfiles
) {

    @Override
    public String toString() {
        if(similarProfiles == null || similarProfiles.isEmpty()) {
            return String.format("no similar profiles found for '%s'", profileName);
        }

        return String.format("profile '%s' not found (did you mean: %s?)",
                profileName,
                String.join(", ", similarProfiles)
        );
    }
}
