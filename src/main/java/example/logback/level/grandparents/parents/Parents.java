package example.logback.level.grandparents.parents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: sonegy@sk.com
 */
public class Parents {
    private static final Logger logger = LoggerFactory.getLogger(Parents.class);

    public void run() {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
