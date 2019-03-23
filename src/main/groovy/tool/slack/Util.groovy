package tool.slack

import java.net.http.HttpClient
import java.net.http.HttpHeaders
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Util {

    private static final Logger logger = LoggerFactory.getLogger(Util.class)

    static void logResponse(HttpResponse<String> response) {
        logger.debug("status {}", response.statusCode())

        HttpHeaders headers = response.headers()
        def sw = new StringWriter()
        headers.map().each { entry ->
            def values = entry.value
            def v
            if (values.size() == 1) {
                v = values[0]
            } else {
                v = values.toString()
            }

            sw.write(System.lineSeparator())
            sw.write(" < ${entry.key}=${v}")
        }
        logger.debug("{}", sw.toString())
    }

    static String toQueryString(Map params) {
        params.findAll { k, v ->
            return k && v
        }.collect { k, v ->
            def encoded = URLEncoder.encode(v, StandardCharsets.UTF_8)
            return "${k}=${encoded}"
        }.join("&")
    }

    static Instant parseTs(String ts) {
        assert ts.contains('.')
        String[] pair = ts.split(/\./)
        assert pair.length == 2
        def (second, nano) = pair.collect { Long.parseLong(it) }
        def instant = Instant.ofEpochSecond(second, nano)
//        def instant = Instant.ofEpochSecond(second)
        return instant
    }

    static String toTs(Instant instant) {
        return "${instant.getEpochSecond()}.${String.format("%06d", instant.getNano())}"
    }

    static createHttpClient() {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(20))
                .build()
        return client
    }

}
