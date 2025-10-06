package org.quickstart.registry;

import org.quickstart.exceptions.QuickStartException;
import org.quickstart.exceptions.RegistryException;
import org.quickstart.exceptions.ServiceError;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;

import static org.quickstart.FileUtils.createFolder;
import static org.quickstart.constants.QuickStartConstants.*;

/**
 * Initializes all necessary files/folders for quickstart
 * */
public final class QuickStartInitializer {

    private final static String JSON_BRACES = "{\n}";

    private QuickStartInitializer() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated.");
    }

    public static void initQuickStart() throws QuickStartException {
        initBaseDirectory();
        initRegistryFile();
        initProfilesFolder();
        initTempFolder();
    }

    /**
     * Creates the base configuration directory (~/.quickstart/).
     */
    private static void initBaseDirectory() throws QuickStartException {
        try {
            createFolder(BASE_PATH);
        } catch (AccessDeniedException e) {
            throw new QuickStartException(
                    new ServiceError(
                            "cannot create config directory",
                            String.format("run `chmod 755 %s` or check parent directory permissions", USER_HOME),
                            e)
            );
        } catch (IOException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("space")) {
                throw new QuickStartException(
                        new ServiceError(
                                "insufficient disk space",
                                "free up space on your home directory",
                                e)
                );
            }

            throw new QuickStartException(
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
    private static void initProfilesFolder() throws QuickStartException {
        try {
            createFolder(PROFILE_BASE_PATH);
        } catch (IOException e) {
            throw new QuickStartException(
                    new ServiceError(
                            "cannot create profiles directory",
                            String.format("try `mkdir -p %s` manually to check permissions", PROFILE_BASE_PATH),
                            e)
            );
        }
    }

    private static void initTempFolder() throws QuickStartException {
        try {
            createFolder(TEMP_BASE_PATH);
        } catch (IOException e) {
            throw new QuickStartException(
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
    private static void initRegistryFile() throws QuickStartException {
        try {
            Files.write(REGISTRY_PATH, JSON_BRACES.getBytes(), StandardOpenOption.CREATE);
        } catch (AccessDeniedException e) {
            throw new QuickStartException(
                    new ServiceError(
                            "permission denied creating registry file",
                            String.format("run `touch %s` to test file creation permissions", REGISTRY_PATH),
                            e)
            );
        } catch (NoSuchFileException e) {
            throw new QuickStartException(
                    new ServiceError(
                            "registry directory does not exist",
                            "run `quickstart init` first to create the config directory",
                            e)
            );
        } catch (IOException e) {
            throw new QuickStartException(
                    new ServiceError(
                            String.format("failed to create `%s`", REGISTRY_PATH),
                            "check disk space with `df -h` and directory permissions",
                            e)
            );
        }
    }
}