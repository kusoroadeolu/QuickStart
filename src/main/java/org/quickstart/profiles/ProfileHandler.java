package org.quickstart.profiles;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.quickstart.compose.ComposeFile;
import org.quickstart.dtos.ProfileDeleteResult;
import org.quickstart.dtos.ProfileDto;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Stream;

import static org.quickstart.constants.QuickStartConstants.*;

public final class ProfileHandler {

    private static final LevenshteinDistance levenshteinDistance = LevenshteinDistance.getDefaultInstance();

    private ProfileHandler(){

    }

    public void createProfile(String profileName) throws ProfileException{
        Path userPath = constructProfile(profileName);
        try {
            Files.createFile(userPath);
        }catch (FileAlreadyExistsException e){
            throw new ProfileException(
                    new ServiceError(
                            String.format("profile '%s' already exists", profileName),
                            "use a different name or delete the existing profile first"
                    )
            );
        }catch(IOException e){
            throw new ProfileException(
                    new ServiceError(
                            "cannot create profile",
                            "check permissions for ~/.quickstart/profiles/",
                            e)
            );
        }
    }



    /**
     * Reads and returns the contents of a profile file as plain text.
     *
     * @param profileName the name of the profile to export
     * @return the raw YAML content of the profile
     * @throws ProfileException if the profile cannot be read
     */
    public String exportProfileContentAsText(String profileName) throws ProfileException {
        ensureProfileExists(profileName);
        Path profilePath = constructProfile(profileName);

        try {
            return Files.readString(profilePath);
        } catch (IOException e) {
            throw new ProfileException(
                    new ServiceError(
                            String.format("cannot read profile '%s'", profileName),
                            "check file permissions or verify that ~/.quickstart/profiles/ is accessible",
                            e
                    )
            );
        }
    }

    /**
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public void exportFromProfile(String profileName, String fileName) throws ProfileException{
        ensureProfileExists(profileName);
        try{
            copyYaml(profileName, fileName, ProfileDecision.FROM_PROFILE);
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            String.format("cannot export profile '%s'", profileName),
                            "check if the profile exists and target directory is writable",
                            e)
            );
        }
    }

    /**
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public void importToProfile(String profileName, String fileName) throws ProfileException{
        ensureProfileExists(profileName);
        try{
            copyYaml(fileName, profileName, ProfileDecision.TO_PROFILE);
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            String.format("cannot import '%s' to profile", fileName),
                            "ensure the source file exists and is readable",
                            e)
            );
        }
    }


    /**
     * Copies the content of a yaml file
     * @param sourceName The name of the source file
     * @param targetName The name of the target file
     * @param decision an enum value to check if we're importing to a profile or exporting to a profile
     * */
    private void copyYaml(String sourceName, String targetName, ProfileDecision decision) throws IOException {
        Path source = decide(sourceName, decision.opposite()); //If we're importing to a profile or user dir, the source should be the opposite of our target
        Path target = decide(targetName, decision);

        String str = Files.readString(source);
        Files.writeString(target, str, StandardOpenOption.TRUNCATE_EXISTING);

    }

    /**
     * Checks if a profile exists (case-sensitive).
     *
     * @param profileName the name of the profile to check
     * @throws ProfileException if the profile does not exist
     */
    public void ensureProfileExists(String profileName) throws ProfileException {
        Path profilePath = constructProfile(profileName);
        if (!Files.exists(profilePath)) {
            throw new ProfileException(
                    new ServiceError(
                            String.format("profile '%s' not found", profileName),
                            new ProfileDto(profileName, findSimilarProfiles(profileName)).toString()
                    )
            );
        }
    }

    public boolean doesProfileExist(String profileName) throws ProfileException{
        Path profilePath = constructProfile(profileName);
        return Files.exists(profilePath);
    }


    /**
     *Decides if we should return a profile path or dir path
     * @param decision An enum flag that decides whether to create a profile or dir path
     * @param fileName The name of the file
     * @return A representation of the path
     * */
    private Path decide(String fileName, ProfileDecision decision){
        return decision == ProfileDecision.TO_PROFILE ? constructProfile(fileName) : constructDirPath(fileName);
    }


    /**
     * Generates a user-friendly summary of all saved profiles.
     *
     * @return A formatted string listing all profiles, or a helpful message if none exist.
     */
    public String listAllProfiles() {
        try (Stream<Path> paths = Files.walk(PROFILE_BASE_PATH, 1)) {
            List<String> profiles = paths
                    .filter(Files::isRegularFile)
                    .map(p -> p.getFileName().toString())
                    .sorted()
                    .toList();

            if (profiles.isEmpty()) {
                return """
                   no profiles found.
                   hint: create one with `quickstart profile create <name>`
                   """;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("available profiles:\n");
            profiles.forEach(p -> sb.append("  - ").append(p).append("\n"));
            sb.append("\nuse `quickstart profile show <name>` to view a profile's contents");

            return sb.toString().trim();

        } catch (IOException e) {
            throw new ProfileException(
                    new ServiceError(
                            "cannot list profiles",
                            "check if ~/.quickstart/profiles/ exists and is readable",
                            e)
            );
        }
    }


    /**
     * Deletes a profile
     * @param profileName The name of the profile
     * */
    public void deleteProfile(String profileName) throws ProfileException{
        ensureProfileExists(profileName);
        Path profilePath = constructProfile(profileName);

        try {
            Files.delete(profilePath);
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            String.format("cannot delete profile '%s'", profileName),
                            "check file permissions",
                            e)
            );
        }

    }

    /**
     * Deletes all the profiles for a user
     * @return A profile dto of all the successful and failed deletes
     *
     * */
    public ProfileDeleteResult deleteAllProfiles() throws ProfileException{
        List<String> failedDeletes = new ArrayList<>();
        List<String> successDeletes = new ArrayList<>();

        try(Stream<Path> paths = Files.walk(PROFILE_BASE_PATH)){
            paths.filter(Objects::nonNull)
                    .filter(Files::isRegularFile)
                    .forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                    successDeletes.add(p.toFile().getName());
                }catch (IOException e){
                    failedDeletes.add(p.toFile().getName());
                }
            });
            return new ProfileDeleteResult(successDeletes, failedDeletes);
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            "cannot access profiles directory",
                            "check if ~/.quickstart/profiles/ exists",
                            e)
            );
        }
    }

    /**
     * Runs the docker content of a profile
     * @param profileName The name of the profile to run
     * @throws ProfileException on IOException
     * */
    public ProfileDto runProfile(String profileName) throws ProfileException {
        ensureProfileExists(profileName);
        Path path = constructProfile(profileName);

        try(ComposeFile composeFile = new ComposeFile(path, profileName, true)){
            composeFile.runTempFile();
            return new ProfileDto(profileName, Collections.emptySet());
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            "cannot create temp compose file",
                            "check disk space and /tmp directory permissions",
                            e)
            );
        } catch (InterruptedException e) {
            throw new ProfileException(
                    new ServiceError(
                            "docker command was interrupted",
                            "try running the profile again",
                            e)
            );
        }
    }


    //Finds profile names similar to that the user inputted
    private Set<String> findSimilarProfiles(String profileName) {
        Set<String> similarProfiles = new HashSet<>();

        try(Stream<Path> paths = Files.walk(PROFILE_BASE_PATH)){
             paths.map(p -> p.getFileName().toString())
                     .forEach(p -> {
                        int dist = levenshteinDistance.apply(profileName, p);
                        if(dist <= SIMILARITY_DISTANCE){
                            similarProfiles.add(p);
                        }
                    });
        }catch (IOException e){
            throw new ProfileException(new ServiceError(
                    "cannot access profiles directory",
                    "verify ~/.quickstart/profiles/ directory exists and is readable",
                    e
            ));
        }

        return similarProfiles;
    }


    //Constructs a profile path
    private Path constructProfile(String profileName) throws ProfileException{
        if(profileName == null || profileName.isEmpty()){
            throw new ProfileException(
                    new ServiceError(
                            "profile name required",
                            "provide a valid profile name"
                    )
            );
        }

        profileName = profileName + YML_EXTENSION;
        return Path.of(PROFILE_BASE_PATH.toString(), profileName);
    }

    //Constructs the dir path for a file in the user's dir
    private Path constructDirPath(String fileName){
        Path file = Path.of(USER_DIR.toString(), fileName);
        if(!Files.exists(file)){
            throw new ProfileException(
                    new ServiceError(
                            String.format("file '%s' not found", file.getFileName()),
                            "check the file path and try again"
                    )
            );
        }
        return file;
    }

    public static ProfileHandler getInstance(){
        return ProfileHandlerHolder.INSTANCE;
    }

    enum ProfileDecision {
        TO_PROFILE,
        FROM_PROFILE;

        public ProfileDecision opposite(){
            return this == TO_PROFILE ? FROM_PROFILE : TO_PROFILE;
        }
    }

    private static class ProfileHandlerHolder{
        private static final ProfileHandler INSTANCE = new ProfileHandler();
    }

}
