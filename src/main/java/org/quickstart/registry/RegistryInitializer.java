package org.quickstart.registry;

import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

import static org.quickstart.FileUtils.createFile;
import static org.quickstart.FileUtils.createFolder;
import static org.quickstart.constants.QuickStartConstants.*;

/**
 * Initializes all necessary files/folders for quickstart
 * */
public final class RegistryInitializer {

    public static void initRegistry() throws RegistryException {
        initBaseDirectory();
        initRegistryFile();
        initProfilesFolder();
        initTempFolder();
    }

    /**
     * Creates the base configuration directory (~/.quickstart/).
     */
    public static void initBaseDirectory() throws RegistryException {
        try {
            createFolder(BASE_PATH);
        } catch (AccessDeniedException e) {
            throw new RegistryException(
                    new ServiceError(
                            "cannot create config directory",
                            String.format("run `chmod 755 %s` or check parent directory permissions", USER_HOME),
                            e)
            );
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("space")) {
                throw new RegistryException(
                        new ServiceError(
                                "insufficient disk space",
                                "free up space on your home directory",
                                e)
                );
            }

            throw new RegistryException(
                    new ServiceError(
                            String.format("failed to create directory `%s`", BASE_PATH),
                            "ensure the parent directory exists and is writable",
                            e)
            );
        }
    }

    /**
     * Creates the profiles subdirectory (~/.quickstart/profiles/).
     */
    public static void initProfilesFolder() throws RegistryException {
        try {
            createFolder(PROFILE_BASE_PATH);
        } catch (IOException e) {
            throw new RegistryException(
                    new ServiceError(
                            "cannot create profiles directory",
                            String.format("try `mkdir -p %s` manually to check permissions", PROFILE_BASE_PATH),
                            e)
            );
        }
    }

    public static void initTempFolder() throws RegistryException {
        try {
            createFolder(TEMP_BASE_PATH);
        } catch (IOException e) {
            throw new RegistryException(
                    new ServiceError(
                            "cannot create profiles directory",
                            String.format("try `mkdir -p %s` manually to check permissions", PROFILE_BASE_PATH),
                            e)
            );
        }
    }

    /**
     * Creates the main registry file (~/.quickstart/registry.yaml).
     */
    public static void initRegistryFile() throws RegistryException {
        try {
            Files.write(REGISTRY_PATH, "{\n}".getBytes(), StandardOpenOption.CREATE);
        } catch (AccessDeniedException e) {
            throw new RegistryException(
                    new ServiceError(
                            "permission denied creating registry file",
                            String.format("run `touch %s` to test file creation permissions", REGISTRY_PATH),
                            e)
            );
        } catch (NoSuchFileException e) {
            throw new RegistryException(
                    new ServiceError(
                            "registry directory does not exist",
                            "run `quickstart init` first to create the config directory",
                            e)
            );
        } catch (IOException e) {
            throw new RegistryException(
                    new ServiceError(
                            String.format("failed to create `%s`", REGISTRY_PATH),
                            "check disk space with `df -h` and directory permissions",
                            e)
            );
        }
    }
}