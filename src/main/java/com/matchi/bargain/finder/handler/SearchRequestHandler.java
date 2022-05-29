package com.matchi.bargain.finder.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchi.bargain.finder.enumeration.EnvironmentType;
import com.matchi.bargain.finder.exception.InvalidRequestException;
import com.matchi.bargain.finder.model.Court;
import com.matchi.bargain.finder.model.RequestParameters;
import org.joda.time.DateTime;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.matchi.bargain.finder.CommonUtils.DATE_TIME_FORMATTER;

public class SearchRequestHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final String ENVIRONMENT_KEY = "environment";
    private static final String DATE_KEY = "date";
    private static final String CITY_KEY = "city";

    private final SearchRequestHandlerHelper searchRequestHandlerHelper;
    private final ObjectMapper mapper;

    public SearchRequestHandler() {
        searchRequestHandlerHelper = new SearchRequestHandlerHelperImpl();
        mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            RequestParameters requestParameters = getAndValidateRequestQueryParameters(requestEvent);
            List<Court> courts = searchRequestHandlerHelper.getCourts(requestParameters);
            String responseBody = mapper.writeValueAsString(courts);

            return new APIGatewayProxyResponseEvent()
                    .withBody(responseBody)
                    .withStatusCode(200);

        } catch (InvalidRequestException e) {
            //TODO: log
            return new APIGatewayProxyResponseEvent()
                    .withBody(e.getMessage())
                    .withStatusCode(400);
        } catch (RuntimeException | JsonProcessingException e) {
            //TODO: log
            return new APIGatewayProxyResponseEvent()
                    .withBody("Error happened while retrieving courts")
                    .withStatusCode(500);
        }
    }

    private RequestParameters getAndValidateRequestQueryParameters(APIGatewayProxyRequestEvent requestEvent) {
        Map<String, String> queryStringParameters = requestEvent.getQueryStringParameters();

        LocalDateTime dateTime = getParameter((key) -> LocalDateTime.parse(queryStringParameters.get(DATE_KEY), DATE_TIME_FORMATTER), DATE_KEY);
        EnvironmentType environment = getParameter((key) -> EnvironmentType.valueOf(queryStringParameters.get(key)), ENVIRONMENT_KEY);
        String city = getParameter(queryStringParameters::get, CITY_KEY);

        return new RequestParameters(dateTime, environment, city);
    }

    private <T> T getParameter(Function<String, T> function, String key) {
        try {
            return function.apply(key);
        } catch (RuntimeException e) {
            throw new InvalidRequestException(String.format("Missing or invalid query parameter %s", key));
        }
    }
}
