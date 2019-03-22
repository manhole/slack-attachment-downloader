package tool.slack

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppMain {

    private static final Logger logger = LoggerFactory.getLogger(AppMain)

    static void main(String[] args) {
        logger.debug("START")
        String token = System.getenv("TOKEN")
        String channel =  System.getenv("CHANNEL")
        logger.debug("token: {}", token)
        logger.debug("channel: {}", channel)

        try {
            new AttachmentDownloader(token: token, channel: channel, savePath: "downloads").execute()
        } catch (Throwable e) {
            logger.error("error", e)
        }
        logger.debug("END")
    }
}
