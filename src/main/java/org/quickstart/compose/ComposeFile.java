package org.quickstart.compose;

import org.quickstart.FileUtils;
import org.quickstart.processes.ProcessStarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ComposeFile implements AutoCloseable{

    private final static String BASE_COMMAND = "docker-compose";
    private final static String FILE_COMMAND = "-f";
    private final static String START_COMMAND = "up";
    private final static String DETACH_COMMAND = "-d";
    private final Path tempFilePath;
    private final String yamlContent;
    private boolean isProfile = false;

    public ComposeFile(Path source, String tempFileName, boolean isProfile) throws IOException {
        this.yamlContent = readSource(source);
        this.tempFilePath = source;
        this.isProfile = isProfile;
    }

    public ComposeFile(Path source) throws IOException {
        this(source, null, false);
    }

    public ComposeFile(String yamlContent) throws IOException {
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
        ProcessStarter.startProcess(arr);
    }

    @Override
    public void close() throws IOException {
        if(tempFilePath != null && !isProfile){
            Files.deleteIfExists(tempFilePath);
        }
    }
}
