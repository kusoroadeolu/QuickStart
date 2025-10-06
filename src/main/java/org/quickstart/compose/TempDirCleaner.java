package org.quickstart.compose;

import org.quickstart.constants.QuickStartConstants;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public final class TempDirCleaner {

    private TempDirCleaner() {

    }

    public void checkForTempFiles(){
        try(Stream<Path> paths = Files.walk(QuickStartConstants.TEMP_BASE_PATH)){

            paths.filter(Files::isRegularFile)
                    .forEach(p-> {
                try{
                    Files.deleteIfExists(p);
                }catch (IOException ignored){

                }
            });
        }catch (IOException ignored){

        }
    }


    public static TempDirCleaner getInstance(){
        return TempDirCleanerHolder.INSTANCE;
    }


    private static class TempDirCleanerHolder {
        private static final TempDirCleaner INSTANCE = new TempDirCleaner();
    }


}
