package org.quickstart.constants;

import java.nio.file.Path;
import java.nio.file.Paths;

public class QuickStartConstants {
    protected QuickStartConstants() {}

    public static final String USER_HOME = System.getProperty("user.dir");
    public static final Path BASE_PATH = Paths.get(USER_HOME, ".quickstart").normalize();
    public static final Path REGISTRY_PATH = Path.of(USER_HOME, ".quickstart", "registry.json").normalize();
    public static final Path PROFILE_BASE_PATH   = Path.of(USER_HOME, ".quickstart", "profile").normalize();
    public static final Path TEMP_BASE_PATH   = Path.of(USER_HOME, ".quickstart", "temp").normalize();
    public static final Path USER_DIR = Path.of(".").toAbsolutePath();

}
