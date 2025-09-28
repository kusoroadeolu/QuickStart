package org.quickstart;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.quickstart.FileUtils.createFile;

public class ProfileBuilder {
    public static void createProfile(String profileName){
        String userHome = System.getProperty("user.home");
        Path registryPath = Paths.get(userHome, ".quickstart", profileName + ".yaml");
        //createFile(registryPath);
    }
}
