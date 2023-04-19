import org.junit.Test;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;

public class UrlHandler {
    String testUrl = "http://www.test.com?key1=value+1&key2=value%40%21%242&key3=value%253";

    public static void main(String[] args) {

    }

    @Test
    public void givenURL_whenAnalyze_thenCorrect() throws Exception {
        URI uri = new URI(testUrl);

        assertThat(uri.getScheme(), is("http"));
        assertThat(uri.getHost(), is("www.test.com"));
        assertThat(uri.getRawQuery(), is("key1=value+1&key2=value%40%21%242&key3=value%253"));
    }

    private String encodeValue(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Test
    public void givenRequestParam_whenUTF8Scheme_thenEncode() throws Exception {
        Map<String, String> requestParams = new HashMap<>();
        requestParams.put("key1", "value 1");
        requestParams.put("key2", "value@!$2");
        requestParams.put("key3", "value%3");

        String encodedURL = requestParams.keySet().stream().map(key -> key + "=" + encodeValue(requestParams.get(key))).collect(joining("&", "http://www.test.com?", ""));

        assertThat(testUrl, is(encodedURL));
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }

    @Test
    public void givenRequestParam_whenUTF8Scheme_thenDecodeRequestParams() throws URISyntaxException {
        URI uri = new URI(testUrl);

        String scheme = uri.getScheme();
        String host = uri.getHost();
        String query = uri.getRawQuery();

        String decodedQuery = Arrays.stream(query.split("&")).map(param -> param.split("=")[0] + "=" + decode(param.split("=")[1])).collect(Collectors.joining("&"));

        assertEquals("http://www.test.com?key1=value 1&key2=value@!$2&key3=value%3", scheme + "://" + host + "?" + decodedQuery);
    }

    private String encodePath(String path) {
        path = UriUtils.encodePath(path, "UTF-8");
        return path;
    }

    @Test
    public void givenPathSegment_thenEncodeDecode() {
        String pathSegment = "/Path 1/Path+2";
        String encodedPathSegment = encodePath(pathSegment);
        String decodedPathSegment = UriUtils.decode(encodedPathSegment, "UTF-8");

        assertEquals("/Path%201/Path+2", encodedPathSegment);
        assertEquals("/Path 1/Path+2", decodedPathSegment);
    }

}