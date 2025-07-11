package logger.data;

import logger.pojo.Log;

import java.util.Collection;

public interface Datastore {

    void appendLog(Collection<Log> logs) throws Exception;

    void addLog(Log log);
}
