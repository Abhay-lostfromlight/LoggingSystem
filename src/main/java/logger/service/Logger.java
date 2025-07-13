package logger.service;

import logger.data.Datastore;
import logger.data.Filestore;
import logger.enums.Severity;
import logger.pojo.Log;
import logger.utils.DeepCopyUtil;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Logger {

    //FileStore is a class that follows the rules of Datastore, declaring fileStore as Datastore - polymorphism (fileStore)
    //fileStore is a general interface built upon Datastore that can be used with either files or a database (if you want
    //to change to database from file in the future). So that less code needs to be modified in the future.
    //This makes the Logger class flexible: swap in a different storage implementation without changing the core logic.

    private Datastore fileStore = new Filestore();

    // logTrackSet is a Set for tracking logs temporarily before processing. hashset for no duplicates
    // Needed to collect logs in memory as they come in, acting like a temp storage to collect the batches of addLog.
    // Prevents saving one log at a time, which would be slow. groups them for better performance.

    private Set<Log> logTrackSet =  new HashSet<>();

    // logsProcessingQueue is a Queue for lining up batches of logs (each batch is a Set<Log>)
    // Dequeue allows flexible adding/removing from both ends, but here it's used as a standard queue for processing order.
    // Needed for asynchronous saving: queues batches so background threads can handle them without blocking the main app.
    // This improves responsiveness - logs are processed in sequence, like a conveyor belt of batches heading to storage.

    private Queue<Set<Log>> logProcessingQueue = new ArrayDeque<>();

    private static final String Log_File = "test.log";
    private static final String Backup_File = "test.log.backup";

    private Integer timeout;

    private static Logger logger = null;    //longer stance initialized to null

    //50 clients/workers pool to handle tasks asynchronously
    ExecutorService service = Executors.newFixedThreadPool(50);

    //Returns the singleton instance of Logger.
    //If the instance is null, it creates a new instance.

    public static Logger getInstance(){
        if(logger == null){
            logger = new Logger();
        }
        return logger;
    }

    //add Timestamp based on local time and timezone
    //get stacktrace of currentthread
    //build string
    public void addLog(Log log) {
        log.setTimestamp(new Timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append("Thread stackTrace");
        int elementsToSkip = 2;

        for (int i = elementsToSkip; i < stackTrace.length; i++) {
            sb.append("\tat").append(stackTrace[i].toString()).append("\n");
        }

        log.setThreadId(Long.toString(Thread.currentThread().getId()));
        log.setThreadName(Thread.currentThread().getName());
        log.setSeverity(log.getSeverity() == null ? Severity.LOW : log.getSeverity());

        String formattedStackTrace = sb.toString();
        log.setStackTrace(formattedStackTrace);

        logTrackSet.add(log);
        put(logTrackSet, log);  //defined later further ahead

    }

    public void appendLog() {
        synchronized (Logger.class) {
            put(logProcessingQueue, logTrackSet);
            flushLogProcessingQueue(logTrackSet);
        }

        service.submit(() -> {
            try{
                synchronized (Logger.class) {
                    fileStore.appendLog(logProcessingQueue.peek());
                    logProcessingQueue.remove();
                    System.out.println("Log append success");
                }
            }catch (Exception e){
                deleteLog();
            }
        });
    }

    private <T> void put(Collection<T> logStore, T log) {
        synchronized (Logger.class) {
            try{
                logStore.add(DeepCopyUtil.deepCopy(log));
            }catch (Exception e){
                System.out.println("failed to create and add deep copy");
            }
        }
    }

    private <T> void flushLogProcessingQueue(Collection<T> logStore) {
        logStore.clear();
    }

    private void deleteLog() {
        File file = new File(Log_File);
        if (!file.exists()) {
            System.out.println("no log file found for deletion");
            return;
        }

        try{
            backupLogFile(file);
            fileStore.deleteLog();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //todo access and delete log files
    }

    // Backup the log file to the backup file location
    private void backupLogFile(File file) throws Exception {
        File backup = new File(Backup_File);
        try (java.io.FileInputStream fis = new java.io.FileInputStream(file);
             java.io.FileOutputStream fos = new java.io.FileOutputStream(backup)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            System.out.println("Log file backed up successfully.");
        } catch (Exception e) {
            System.err.println("Failed to backup log file: " + e.getMessage());
            throw e;
        }
    }
}
