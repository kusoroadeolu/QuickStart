package org.quickstart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import static org.quickstart.constants.QuickStartConstants.*;

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

    //Create a  temp yaml file. Note that this file will be deleted after use
    public static Path createAndWriteToTempFile(String fileName, String content) throws IOException {
        //Generate a random ID for each temp file
        String tempName = null;

        if(fileName == null || fileName.isEmpty()){
            tempName = UUID.randomUUID().toString() + YML_EXTENSION;
        }else{
            tempName = fileName;
        }

        Path path = Paths.get(TEMP_BASE_PATH.toString(), tempName);

        Files.writeString(path, content, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
        return path;
    }


}
