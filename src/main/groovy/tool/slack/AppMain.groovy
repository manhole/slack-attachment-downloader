package tool.slack

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppMain {

    private static final Logger logger = LoggerFactory.getLogger(AppMain)

    static void main(String[] args) {
        logger.debug("START")
        URL resource = Thread.currentThread().getContextClassLoader().getResource("config.txt")
        ConfigObject config = new ConfigSlurper().parse(resource)

        try {
            new AttachmentDownloader(token: config.token, channel: config.channel, savePath: "downloads").execute()
        } catch (Throwable e) {
            logger.error("error", e)
        }
        logger.debug("END")
    }
}
