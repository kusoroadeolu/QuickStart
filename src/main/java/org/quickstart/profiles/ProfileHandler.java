package org.quickstart.profiles;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.quickstart.compose.TempComposeFile;
import org.quickstart.dtos.ProfileDeleteResult;
import org.quickstart.dtos.ProfileDto;
import org.quickstart.exceptions.ProfileException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.nio.file.*;
import java.security.AllPermission;
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
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public void importToProfile(String profileName, String fileName) throws ProfileException{
        try{
            copyYaml(fileName, profileName, true);
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
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public void exportFromProfile(String profileName, String fileName) throws ProfileException{
        try{
            copyYaml(profileName, fileName, false);
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
     * Copies the content of a yaml file
     * @param sourceName The name of the source file
     * @param targetName The name of the target file
     * @param toProfile bool value to check if we're importing to a profile or exporting
     * */
    private void copyYaml(String sourceName, String targetName, boolean toProfile) throws IOException {
        Path source = decide(sourceName, !toProfile);
        Path target = decide(targetName, toProfile);

        if(!Files.exists(source)){
            throw new ProfileException(
                    new ServiceError(
                            String.format("file '%s' not found", source.getFileName()),
                            "check the file path and try again"
                    )
            );
        }

        String str = Files.readString(source);
        Files.writeString(target, str, StandardOpenOption.TRUNCATE_EXISTING);

    }


    /**
     *Decides if we should return a profile path or dir path
     * @param toProfile The boolean flag that decides whether to create a profile or dir path
     * @param fileName The name of the file
     * @return A representation of the path
     * */
    private Path decide(String fileName, boolean toProfile){
        return toProfile ? constructProfile(fileName) : constructDirPath(fileName);
    }


    /**
     * Lists all the profiles for a user
     * @return A string of all the profiles
     * */
    public String listAllProfiles(){
        try(Stream<Path> paths = Files.walk(PROFILE_BASE_PATH)){
            StringBuilder result = new StringBuilder();
            paths.forEach(p -> result.append(p.getFileName()).append("\n"));
            return result.toString();
        }catch (IOException e){
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
        if (profileName == null || profileName.isEmpty()){
            throw new ProfileException(
                    new ServiceError(
                            "profile name required",
                            "provide a profile name to delete"
                    )
            );
        }

        Path profilePath = constructProfile(profileName);

        try {
            Files.deleteIfExists(profilePath);
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
            paths.forEach(p -> {
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

    public ProfileDto runProfile(String profileName) throws ProfileException {
        Path path = constructProfile(profileName);

        try{
            if(!Files.exists(path)){
                return new ProfileDto(profileName, findSimilarProfiles(profileName));
            }
        }catch (IOException e){
            throw new ProfileException(
                    new ServiceError(
                            "cannot check profile existence",
                            "verify ~/.quickstart/profiles/ directory permissions",
                            e)
            );
        }

        try(TempComposeFile tempComposeFile = new TempComposeFile(path)){
            tempComposeFile.runTempFile();
            return new ProfileDto("", Collections.emptySet());
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


    //Finds profile names similar to that the user inputed
    private Set<String> findSimilarProfiles(String profileName) throws IOException {
        Set<String> similarProfiles = new HashSet<>();

        try(Stream<Path> paths = Files.walk(PROFILE_BASE_PATH)){
             paths.map(p -> p.getFileName().toString())
                     .forEach(p -> {
                        int dist = levenshteinDistance.apply(profileName, p);
                        if(dist <= SIMILARITY_DISTANCE){
                            similarProfiles.add(p);
                        }
                    });
        }

        return similarProfiles;
    }



    private Path constructProfile(String profileName){
        if(profileName == null || profileName.isEmpty()){
            throw new ProfileException(
                    new ServiceError(
                            "profile name required",
                            "provide a valid profile name"
                    )
            );
        }

        profileName = profileName + YML_EXTENSION;
        return Paths.get(PROFILE_BASE_PATH.toString(), profileName);
    }

    private Path constructDirPath(String fileName){
        return Path.of(USER_DIR.toString(), fileName);
    }

    public static ProfileHandler getInstance(){
        return ProfileHandlerHolder.INSTANCE;
    }

    private static class ProfileHandlerHolder{
        private static final ProfileHandler INSTANCE = new ProfileHandler();
    }

}
