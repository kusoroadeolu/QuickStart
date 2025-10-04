package org.quickstart.compose;

import org.quickstart.FileUtils;
import org.quickstart.processes.ProcessStarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class TempComposeFile implements AutoCloseable{

    private final static String BASE_COMMAND = "docker-compose";
    private final static String FILE_COMMAND = "-f";
    private final static String START_COMMAND = "up";
    private final static String DETACH_COMMAND = "-d";
    private final Path tempFilePath;
    private final String yamlContent;

    public TempComposeFile(Path source, String tempFileName) throws IOException {
        this.yamlContent = readSource(source);
        this.tempFilePath = createTempFile(tempFileName, yamlContent);
    }

    public TempComposeFile(Path source) throws IOException {
        this(source, null);
    }

    public TempComposeFile(String yamlContent) throws IOException {
        this.yamlContent = yamlContent;
        this.tempFilePath = createTempFile(null, yamlContent);
    }

    private Path createTempFile(String fileName, String source) throws IOException {
        return FileUtils.createAndWriteToTempFile(fileName, source);
    }

    //Reads content from the acc file
    private String readSource(Path source) throws IOException, IllegalArgumentException {
        if(source == null){
            throw new IllegalArgumentException();
        }

        return Files.readString(source);
    }

    //Starts the docker command for a temp file
    public void runTempFile() throws IllegalArgumentException, IOException, InterruptedException {

        if(tempFilePath == null || !Files.exists(tempFilePath)){
            throw new IllegalStateException("temp file does not exist");
        }

        String[] arr = {BASE_COMMAND, FILE_COMMAND, tempFilePath.toAbsolutePath().toString(), START_COMMAND, DETACH_COMMAND};
        IO.println("Temp file path: " + tempFilePath);
        ProcessStarter.startProcess(arr);
    }

    @Override
    public void close() throws IOException {
        if(tempFilePath != null){
            Files.deleteIfExists(tempFilePath);
        }
    }
}
