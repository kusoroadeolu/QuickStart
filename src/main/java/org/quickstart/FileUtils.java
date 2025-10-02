package org.quickstart;

import org.quickstart.constants.QuickStartConstants;
import org.quickstart.exceptions.ComposeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

// Helper class for file operations
public class FileUtils {
    public static void createFile(Path filePath) throws IOException {
        if(Files.exists(filePath)){
            return;
        }

        Files.createFile(filePath);

    }

    public static void createFolder(Path folderPath) throws IOException {
        if(Files.exists(folderPath)){
            return;
        }

        Files.createDirectory(folderPath);
    }

    //Create a  temp yaml file. Note that this file must be deleted after use
    public static Path createTempFile(String content) throws IOException {
        //Generate a random ID for each temp file
        String tempName = UUID.randomUUID().toString() + ".yaml";
        Path path = Paths.get(QuickStartConstants.TEMP_BASE_PATH.toString(), tempName);

        Files.writeString(path, content,  StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
        return path;

    }
}
