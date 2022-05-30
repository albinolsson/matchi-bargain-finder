package com.matchi.bargain.finder.handler;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class SearchRequestHandlerTest {

    //@Test
    public void test() {
        SearchRequestHandler searchRequestHandler = new SearchRequestHandler();
        searchRequestHandler.handleRequest(getRequest(), null);
    }

    private APIGatewayProxyRequestEvent getRequest() {
        String formattedDate = LocalDateTime.of(2022, 5, 30, 18, 30).format(DateTimeFormatter.ofPattern("yyyy-MM-dd:HH:mm"));
        return new APIGatewayProxyRequestEvent()
                .withQueryStringParameters(Map.of("environment", "BOTH",
                        "date", formattedDate,
                        "city", URLEncoder.encode("Göteborg", Charset.defaultCharset())));
    }

}