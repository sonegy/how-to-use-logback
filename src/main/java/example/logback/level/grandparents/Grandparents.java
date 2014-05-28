package example.logback.level.grandparents;

import example.logback.level.grandparents.parents.Parents;
import example.logback.level.grandparents.parents.children.Children;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author: sonegy@sk.com
 */
public class Grandparents {
    private static final Logger logger = LoggerFactory.getLogger(Grandparents.class);

    public void run() {
        logger.trace("trace");
        logger.debug("debug");
        logger.info("info");
        logger.warn("warn");
        logger.error("error");
    }

    public static void main(String[] args) {
        new Grandparents().run();
        new Parents().run();
        new Children().run();
    }

}
