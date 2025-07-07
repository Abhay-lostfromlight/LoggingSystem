package logger.pojo;

import java.io.Serializable;
import java.sql.Timestamp;

public class Log implements Serializable {

    private String data;
    private Timestamp timestamp;
    private String threadId;
    private String threadName;

    public Log(String data) {
        this.data = data;
    }
    // todo -

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
}
