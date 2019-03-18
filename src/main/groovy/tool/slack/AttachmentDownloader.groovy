package tool.slack

import static tool.slack.Util.createHttpClient
import static tool.slack.Util.logResponse
import static tool.slack.Util.toQueryString

import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.fasterxml.jackson.databind.ObjectMapper

import groovy.json.JsonSlurper

class AttachmentDownloader {

    private final Logger logger = LoggerFactory.getLogger(getClass())

    // https://api.slack.com/methods/channels.history
    static final String CHANNELS_HISTORY = "https://slack.com/api/channels.history"
    // https://api.slack.com/methods/channels.info
    static final String CHANNELS_INFO = "https://slack.com/api/channels.info"

    private String token
    private String channel
    private String savePath

    private final ObjectMapper mapper = new ObjectMapper()
    private HttpClient client
    private Path saveDir
    private Path stateFilePath
    private DateTimeFormatter dateTimeFormatter

    void execute() {
        client = createHttpClient()
        dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").withZone(ZoneId.systemDefault())
        saveDir = Path.of(savePath).resolve(channel).toAbsolutePath()
        logger.debug("saveDir: {}", saveDir)

        if (!Files.exists(saveDir)) {
            logger.debug("create directory: {}", saveDir)
            Files.createDirectories(saveDir)
        }
        stateFilePath = saveDir.resolve("_state.json")
        final State state = readState()

        if (state.createdAt == null) {
            state.createdAt = channelInfo()
            saveState(state)
        }

        if (!state.alreadyProceededFromChannelCreated) {
            crawlFromChannelCreated(state)
        }
    }

    private void crawlFromChannelCreated(State state) {
        String latest = state.readFrom
        String readStartTs = null
        boolean hasMore = true
        while (hasMore) {
            def params = [:]
            params["token"] = this.token
            params["channel"] = this.channel
            params["latest"] = latest
            //params["oldest"] = state.readTo
            //params["count"] = "1"

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(CHANNELS_HISTORY))
                    .timeout(Duration.ofMinutes(2))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(toQueryString(params), StandardCharsets.UTF_8))
                    .build()

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString())
            logResponse(response)

            def bodyString = response.body()

            def slurper = new JsonSlurper()
            def body = slurper.parseText(bodyString)
            assert body.ok

            body.messages.each { message ->
                logger.debug("message: {}", message)
                def ts = message.ts
                if (readStartTs == null) {
                    readStartTs = ts
                }
                /*
                ファイルへのコメントや、Google Spreadsheetへのリンクはupload=falseになる。
                他のチャネルにアップされた画像のリンクもupload=falseになってしまうが、許容する。
                 */
                if (message.upload) {
                    message.files?.each { file -> eachFile(file) }
                }
                latest = ts
                state.readFrom = ts
                saveState(state)
            }

            hasMore = body.has_more
        }
        state.readTo = readStartTs
        state.alreadyProceededFromChannelCreated = true
        saveState(state)
    }

    private void eachFile(def file) {
        String downloadUrl = file.url_private_download
        if (downloadUrl) {
            // createdとtimestampは常に同じ値?
            assert file.timestamp == file.created

            def fileTimestamp = Instant.ofEpochSecond(file.timestamp)
            String namePart = file.name
            def name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart
            def saveFilePath
            try {
                saveFilePath = saveDir.resolve(name)
            } catch (e) {
                // "foo32*32.png" といったファイル名はファイルシステムに保存できない
                logger.debug("{}: {}, <{}>", e.getClass().getName(), e.getMessage(), name)
                int pos = downloadUrl.lastIndexOf('/')
                namePart = downloadUrl.substring(pos + 1)
                name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart
                saveFilePath = saveDir.resolve(name)
            }

            // 上書きは嫌なので念の為確認しておきます
            if (Files.exists(saveFilePath)) {
                // createdだと被ることがあるので、idを付けます
                name = dateTimeFormatter.format(fileTimestamp) + "-" + namePart + "-${file.id}"
                saveFilePath = saveDir.resolve(name)
                logger.debug("ファイル名が被ったのでidを付けました: {}", saveFilePath)
            }
            // idを付けても被ったらお手上げ
            assert !Files.exists(saveFilePath)

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(downloadUrl))
                    .timeout(Duration.ofMinutes(2))
                    .header("Authorization", "Bearer ${token}")
                    .GET()
                    .build()
            logger.debug("ダウンロードします  : {}, {}", downloadUrl, name)
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofFile(saveFilePath))
            logger.debug("ダウンロードしました: {}", downloadUrl)
            logResponse(response)
        } else {
            // message.upload=true でチェックしていれば、この分岐には到達しないはず。
            logger.debug("url_private_download が無い. {}", file)
        }
    }

    private String channelInfo() {
        def params = [:]
        params["token"] = this.token
        params["channel"] = this.channel

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CHANNELS_INFO))
                .timeout(Duration.ofMinutes(2))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(toQueryString(params), StandardCharsets.UTF_8))
                .build()

        HttpResponse<String> response = this.client.send(request, HttpResponse.BodyHandlers.ofString())
        logResponse(response)

        def bodyString = response.body()
        logger.debug("channelInfo: {}", bodyString)

        def slurper = new JsonSlurper()
        def body = slurper.parseText(bodyString)

        return body.channel.created
    }

    private State readState() {
        if (!Files.exists(stateFilePath)) {
            return new State()
        }
        return mapper.readValue(stateFilePath.toFile(), State)
    }

    private void saveState(State state) {
        mapper.writeValue(stateFilePath.toFile(), state)
    }

}
