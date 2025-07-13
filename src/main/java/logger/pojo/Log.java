package logger.pojo;

import logger.enums.Severity;

import java.io.Serializable;
import java.sql.Timestamp;

public class Log implements Serializable {
    //all the functionality of a log
    private String data;
    private Timestamp timestamp;
    private String threadId;
    private String threadName;
    private Severity severity;
    private String stackTrace;

    //Constructs a Log object with the specified severity and data.
    public Log(Severity severity, String data) {
        this.severity = severity;
        this.data = data;
    }

    //overloading same Log method for  a Log object with the specified data, timestamp, and stack trace.
    public Log(String data, Timestamp timestamp, String stackTrace) {
        this.data = data;
        this.timestamp = timestamp;
        this.stackTrace = stackTrace;
        // Todo: Figure out how to get thread information when user is adding/appending any log and add stackTrace
    }

    public Log(String data) {
        this.data = data;
    }
    //todo

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(Severity severity) {
        this.severity = severity;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    /**
     * Creates a new Log object based on the provided log.
     *
     * @param log The log to copy.
     * @return A new Log object with the same attributes as the provided log.
     */
    public Log getLog(Log log) {
        return new Log(log.getStackTrace(), log.getTimestamp(), log.getData());
    }
}
