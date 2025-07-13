package logger.data;

import logger.pojo.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.concurrent.TimeoutException;

public class Filestore implements Datastore {

    private static final String Log_File = "test.log";

    @Override
    public void addLog(Log log){};

    @Override
    public void appendLog(Collection<Log> logs) throws TimeoutException {
        try {
            File file = new File("test.log");
            try(FileOutputStream fos = new FileOutputStream(file);
                ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))){
                    for (Log log : logs) {
                        oos.writeObject(log);
                    }
                }
            } catch (Exception e) {
                //
            }
        }

    @Override
    public void deleteLog() {
        File file = new File(Log_File);
        if (!file.exists()) {
            System.out.println("No log file found for deletion.");
            return;
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.getChannel().truncate(0);
            System.out.println("Log file cleared successfully.");
        } catch (IOException e) {
            System.err.println("Error during log deletion: " + e.getMessage());
            e.printStackTrace();  // Prints stack trace for debugging
        }
    }

}



