package org.quickstart.compose;

import org.quickstart.FileUtils;
import org.quickstart.processes.ProcessStarter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TempComposeFile implements AutoCloseable{

    private static final ProcessStarter STARTER = new ProcessStarter();
    private final Path tempFilePath;
    private final String yamlContent;

    public TempComposeFile(Path source, String tempFileName) throws IOException, IllegalArgumentException {
        yamlContent = readSource(source);
        tempFilePath = createTempFile(tempFileName, yamlContent);
    }

    public TempComposeFile(Path source) throws IOException, IllegalArgumentException {
        this(source, null);
    }

    public TempComposeFile(String tempFileName, String yamlContent) throws IOException {
        this.yamlContent = yamlContent;
        tempFilePath = createTempFile(tempFileName, yamlContent);
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
    public void runTempFile() throws IllegalArgumentException{
        String[] arr = {"-f", tempFilePath.toAbsolutePath().toString(), "up", "-d"};
        STARTER.startProcess(arr);
    }

    @Override
    public void close() throws IOException {
        if(tempFilePath != null){
            Files.deleteIfExists(tempFilePath);
        }
    }
}
