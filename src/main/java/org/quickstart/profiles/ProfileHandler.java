package org.quickstart.profiles;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import org.quickstart.compose.TempComposeFile;
import org.quickstart.dtos.ProfileDeleteResult;
import org.quickstart.exceptions.ProfileException;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.quickstart.constants.QuickStartConstants.*;

public class ProfileHandler {



    public static void createProfile(String profileName) throws ProfileException{
        Path userPath = constructAndValidateProfile(profileName);
        try{
            Files.createFile(userPath);
        }catch(IOException e){
            throw new ProfileException("Profile Creation Failed");
        }
    }

    /**
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public static void importToProfile(String profileName, String fileName) throws ProfileException{
        try{
            copyYaml(fileName, profileName, true);
        }catch (IOException e){
            throw new ProfileException("Profile Import Failed");
        }
    }

    /**
     * Imports from a yaml file to a profile. We assume the file, is in the user's base dir
     * @param fileName The name of the file we're importing from
     * @param profileName The name of the profile
     * */
    public static void exportToProfile(String profileName, String fileName) throws ProfileException{
        try{
            copyYaml(profileName, fileName, false);
        }catch (IOException e){
            throw new ProfileException("Profile Export Failed");
        }
    }



    /**
     * Copies the content of a yaml file
     * @param sourceName The name of the source file
     * @param targetName The name of the target file
     * @param toProfile bool value to check if we're importing to a profile or exporting
     * */
    private static void copyYaml(String sourceName, String targetName, boolean toProfile) throws IOException {
        Path source = decide(sourceName, toProfile);
        Path target = decide(targetName, !toProfile);

        if(!Files.exists(source)){
            throw new ProfileException(String.format("File %s does not exist", source));
        }

        YAMLMapper mapper = new YAMLMapper();
        JsonNode node = mapper.readTree(source.toFile());
        Files.writeString(target, mapper.writeValueAsString(node));

    }


    //Decides if we should return a profile path or dir path
    private static Path decide(String fileName, boolean toProfile){
        return toProfile ? constructAndValidateProfile(fileName) : Path.of(USER_DIR.toString(), fileName);
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
            throw new ProfileException("Profile Listing Failed");
        }
    }

    /**
     * Deletes a profile
     * @param profileName The name of the profile
     * */
    public void deleteProfile(String profileName) throws ProfileException{
        if (profileName == null || profileName.isEmpty()){
            throw new ProfileException("Profile name cannot be empty");
        }

        Path profilePath = constructAndValidateProfile(profileName);

        try {
            Files.delete(profilePath);
        }catch (IOException e){
            throw new ProfileException("Profile Deletion Failed");
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
            throw new ProfileException("Failed to get profile parent path");
        }
    }

    public void runProfile(String profileName) throws ProfileException{
        Path path = constructAndValidateProfile(profileName);
        try(TempComposeFile tempComposeFile = new TempComposeFile(path)){
            tempComposeFile.runTempFile();
        }catch (IOException e){
            throw new ProfileException("An error occurred?");
        }
    }



    private static Path constructAndValidateProfile(String profileName){
        if(profileName == null || profileName.isEmpty()){
            throw new ProfileException("Profile name cannot be null or empty");
        }

        profileName = profileName + YAML_EXTENSION;

        Path userPath = Paths.get(PROFILE_BASE_PATH.toString(), profileName);

        if(Files.exists(userPath)){
           throw new ProfileException("Profile already exists");
        }

        return userPath;
    }

}
