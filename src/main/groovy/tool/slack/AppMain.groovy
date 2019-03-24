package tool.slack

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class AppMain {

    private static final Logger logger = LoggerFactory.getLogger(AppMain)

    static void main(String[] args) {
        logger.debug("START")
        String token = System.getenv("TOKEN")
        String channel = System.getenv("CHANNEL")
        String publicChannelStr = System.getenv("PUBLIC")
        boolean publicChannel = Boolean.parseBoolean(publicChannelStr)
        logger.debug("token: {}", token)
        logger.debug("channel: {}", channel)
        logger.debug("public: {}", publicChannel)

        try {
            new AttachmentDownloader(
                    token: token,
                    channel: channel,
                    publicChannel: publicChannel,
                    savePath: "downloads",
            ).execute()
        } catch (Throwable e) {
            logger.error("error", e)
        }
        logger.debug("END")
    }
}
