package logger.service;

import logger.data.Datastore;
import logger.data.Filestore;
import logger.pojo.Log;
import logger.utils.DeepCopyUtil;

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

    public void addLog(Log log) {
        log.setTimestamp(new Timestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)));

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        sb.append("Thread stackTrace");
        int elementsToSkip = 2;

        for (int i = elementsToSkip; i < stackTrace.length; i++) {
            sb.append("\tat").append(stackTrace[i].toString()).append("\n");
        }

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
                deleteLogs();
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

    private void deleteLogs() {
        //todo access and delete log files
    }
}
