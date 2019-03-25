package tool.slack

//import static tool.slack.Util.createHttpClient
//import static tool.slack.Util.logResponse
//import static tool.slack.Util.toQueryString

//import java.net.http.HttpClient
//import java.net.http.HttpRequest
//import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.json.JsonSlurper

class AttachmentDownloader {

//    private final Logger logger = LoggerFactory.getLogger(getClass())

    // https://api.slack.com/methods/channels.history
    static final String CHANNELS_HISTORY = "https://slack.com/api/channels.history"
    // https://api.slack.com/methods/channels.info
    static final String CHANNELS_INFO = "https://slack.com/api/channels.info"

    // https://api.slack.com/methods/groups.history
    static final String GROUPS_HISTORY = "https://slack.com/api/groups.history"
    // https://api.slack.com/methods/groups.info
    static final String GROUPS_INFO = "https://slack.com/api/groups.info"

    private String token
    private String channel
    private boolean publicChannel
    private String savePath

    private final ObjectMapper mapper = new ObjectMapper()
    private final JsonSlurper slurper = new JsonSlurper()

//    private HttpClient client
    private Path saveDir
    private Path stateFilePath
    private DateTimeFormatter dateTimeFormatter
    private URI historyApi
    private URI infoApi

    void execute() {
//        client = createHttpClient()
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.systemDefault())

        if (publicChannel) {
            historyApi = URI.create(CHANNELS_HISTORY)
            infoApi = URI.create(CHANNELS_INFO)
        } else {
            historyApi = URI.create(GROUPS_HISTORY)
            infoApi = URI.create(GROUPS_INFO)
        }

        saveDir = Path.of(savePath).resolve(channel).toAbsolutePath()
        println("saveDir: ${saveDir}")

        if (!Files.exists(saveDir)) {
            println("create directory: ${saveDir}")
            Files.createDirectories(saveDir)
        }
        stateFilePath = saveDir.resolve("_state.json")
        State state = readState()

        if (state == null) {
            state = new State()
//            def info = channelInfo()
//            println(info)
        }

        crawlChannel(state)
    }

    private void crawlChannel(State state) {
//        boolean hasMore = true
//        while (hasMore) {
//            def params = [:]
//            params["token"] = this.token
//            params["channel"] = this.channel
//            params["latest"] = state.readingPosition
//            params["oldest"] = state.oldest
//            //params["count"] = "1"
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(historyApi)
//                    .timeout(Duration.ofMinutes(2))
//                    .header("Content-Type", "application/x-www-form-urlencoded")
//                    .POST(HttpRequest.BodyPublishers.ofString(toQueryString(params), StandardCharsets.UTF_8))
//                    .build()
//
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString())
//            logResponse(response)
//
//            def bodyString = response.body()
//            def body = slurper.parseText(bodyString)
//            assert body.ok
//
//            body.messages.each { message ->
//                println("message: {}", message)
//                def ts = message.ts
//                assert ts != null
//                if (state.latest == null) {
//                    state.latest = ts
//                }
//                /*
//                ファイルへのコメントや、Google Spreadsheetへのリンクはupload=falseになる。
//                他のチャネルにアップされた画像のリンクもupload=falseになってしまうが、許容する。
//                 */
//                if (message.upload) {
//                    message.files?.each { file -> eachFile(file) }
//                }
//                state.readingPosition = ts
//                saveState(state)
//            }
//
//            hasMore = body.has_more
//        }
//
//        if (state.latest) {
//            state.oldest = state.latest
//            state.latest = null
//        }
//        state.readingPosition = null
//        saveState(state)
    }

//    private void eachFile(def file) {
//        String downloadUrl = file.url_private_download
//        if (downloadUrl) {
//            // createdとtimestampは常に同じ値?
//            assert file.timestamp == file.created
//
//            def fileTimestamp = Instant.ofEpochSecond(file.timestamp)
//            String namePart = file.name
//            def name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart
//            def saveFilePath
//            try {
//                saveFilePath = saveDir.resolve(name)
//            } catch (e) {
//                // "foo32*32.png" といったファイル名はファイルシステムに保存できない
//                println("{}: {}, <{}>", e.getClass().getName(), e.getMessage(), name)
//                int pos = downloadUrl.lastIndexOf('/')
//                namePart = downloadUrl.substring(pos + 1)
//                name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart
//                saveFilePath = saveDir.resolve(name)
//            }
//
//            // 上書きは嫌なので念の為確認しておきます
//            if (Files.exists(saveFilePath)) {
//                // createdだと被ることがあるので、idを付けます
//                name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart + "-${file.id}"
//                saveFilePath = saveDir.resolve(name)
//                println("ファイル名が被ったのでidを付けました: {}", saveFilePath)
//            }
//            // idを付けても被ったらお手上げ
//            assert !Files.exists(saveFilePath)
//
//            HttpRequest request = HttpRequest.newBuilder()
//                    .uri(URI.create(downloadUrl))
//                    .timeout(Duration.ofMinutes(2))
//                    .header("Authorization", "Bearer ${token}")
//                    .GET()
//                    .build()
//
//            def tmpFile = saveFilePath.resolveSibling(saveFilePath.getFileName().toString() + ".downloading")
//            println("ダウンロードします  : {}, {}", downloadUrl, name)
//            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tmpFile))
//            Files.move(tmpFile, saveFilePath, StandardCopyOption.ATOMIC_MOVE)
//            println("ダウンロードしました: {}", downloadUrl)
//            logResponse(response)
//        } else {
//            // message.upload=true でチェックしていれば、この分岐には到達しないはず。
//            println("url_private_download が無い. {}", file)
//        }
//    }

//    private Object channelInfo() {
//        def params = [:]
//        params["token"] = this.token
//        params["channel"] = this.channel
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(infoApi)
//                .timeout(Duration.ofMinutes(2))
//                .header("Content-Type", "application/x-www-form-urlencoded")
//                .POST(HttpRequest.BodyPublishers.ofString(toQueryString(params), StandardCharsets.UTF_8))
//                .build()
//
//        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString())
//        logResponse(response)
//
//        def bodyString = response.body()
//        println("channelInfo: {}", bodyString)
//
//        def body = slurper.parseText(bodyString)
//
//        return body.channel
//    }

    private State readState() {
        if (!Files.exists(stateFilePath)) {
            return null
        }
        return mapper.readValue(stateFilePath.toFile(), State)
    }

    private void saveState(State state) {
        mapper.writeValue(stateFilePath.toFile(), state)
    }

}
