package logger;

import logger.pojo.Log;
import logger.service.Logger;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
            Logger logger = Logger.getInstance();
            logger.addLog(new Log("start logger"));
            logger.addLog(new Log("enter into file"));
            logger.addLog(new Log("got exception"));
            logger.addLog(new Log("end logger"));

            logger.appendLog();


    }
}
