package tool.slack

class AppMain {

    static void main(String[] args) {
        println("START")
        String token = System.getenv("TOKEN")
        String channel = System.getenv("CHANNEL")
        String publicChannelStr = System.getenv("PUBLIC")
        boolean publicChannel = Boolean.parseBoolean(publicChannelStr)
        println("token: ${token}")
        println("channel:${channel}")
        println("public: ${publicChannel}")

        if (!token) {
            throw new IllegalArgumentException("token")
        }
        if (!channel) {
            throw new IllegalArgumentException("channel")
        }
        try {
            new AttachmentDownloader(
                    token: token,
                    channel: channel,
                    publicChannel: publicChannel,
                    savePath: "downloads",
            ).execute()
        } catch (Throwable e) {
            e.printStackTrace()
        }
        println("END")
    }
}
