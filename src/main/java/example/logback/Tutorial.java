package example.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: sonegy@sk.com
 */
public class Tutorial {
    private static final Logger logger = LoggerFactory.getLogger(Tutorial.class);

    public static void main(String[] args) {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }
}
