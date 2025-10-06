package org.quickstart.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QuickStartConstants {
    protected QuickStartConstants() {}

    private static final String QUICK_START_FOLDER = ".quickstart";
    public static final String USER_HOME = System.getProperty("user.home");
    public static final Path BASE_PATH = Paths.get(USER_HOME, QUICK_START_FOLDER).normalize();
    public static final Path REGISTRY_PATH = Path.of(USER_HOME, QUICK_START_FOLDER, "registry.json").normalize();
    public static final Path PROFILE_BASE_PATH   = Path.of(USER_HOME, QUICK_START_FOLDER, "profile").normalize();
    public static final Path TEMP_BASE_PATH   = Path.of(USER_HOME, QUICK_START_FOLDER, "temp").normalize();
    public static final Path USER_DIR = Path.of("").toAbsolutePath();
    public static final String YML_EXTENSION = ".yml";
    public static final String YAML_EXTENSION = ".yaml";
    public static final int SIMILARITY_DISTANCE = 2;

}
