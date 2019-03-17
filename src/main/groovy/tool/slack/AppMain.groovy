package tool.slack

class AppMain {

    static void main(String[] args) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource("config.txt")
        ConfigObject config = new ConfigSlurper().parse(resource)

        new AttachmentDownloader(token: config.token, channel: config.channel, savePath: "downloads").execute()
    }
}
