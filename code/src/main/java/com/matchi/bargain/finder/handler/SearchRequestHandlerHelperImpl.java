package com.matchi.bargain.finder.handler;

import com.matchi.bargain.finder.enumeration.EnvironmentType;
import com.matchi.bargain.finder.model.Court;
import com.matchi.bargain.finder.model.RequestParameters;
import com.matchi.bargain.finder.model.TransformData;
import com.matchi.bargain.finder.transform.CourtTransformer;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.matchi.bargain.finder.CommonUtils.HTTP_CLIENT;

public class SearchRequestHandlerHelperImpl implements SearchRequestHandlerHelper {

    @Override
    public List<Court> getCourts(RequestParameters requestParameters) {
        Map<Object, Object> data = getFormDataMap(requestParameters);

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.matchi.se/book/findFacilities"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(ofFormData(data))
                .build();

        try {
            List<Court> courts = new ArrayList<>();
            int offset = 0;

            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            LocalDateTime localDateTime = requestParameters.getLocalDateTime();

            while(responseBody.length() > 2000) {
                TransformData transformData = new TransformData(responseBody, Integer.toString(localDateTime.getHour()), Integer.toString(localDateTime.getMinute()));
                courts.addAll(CourtTransformer.INSTANCE.transform(transformData));

                offset += 10;
                request = getHttpRequest(offset, data);
                responseBody = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
            }

            return courts.stream()
                    .sorted(Comparator.comparing(Court::getHourlyRate))
                    .collect(Collectors.toList());
        } catch (IOException | InterruptedException e) {
            if(e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Error happened while retrieving courts", e);
        }
    }

    private HttpRequest getHttpRequest(int offset, Map<Object, Object> data) {
        data.put("offset", offset);
        return HttpRequest.newBuilder(URI.create("https://www.matchi.se/book/findFacilities"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(ofFormData(data))
                .build();
    }

    public HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
        var builder = new StringBuilder();
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            if (builder.length() > 0) {
                builder.append("&");
            }
            builder.append(URLEncoder.encode(entry.getKey().toString(), StandardCharsets.UTF_8));
            builder.append("=");
            builder.append(URLEncoder.encode(entry.getValue().toString(), StandardCharsets.UTF_8));
        }
        return HttpRequest.BodyPublishers.ofString(builder.toString());
    }

    private Map<Object, Object> getFormDataMap(RequestParameters requestParameters) {
        EnvironmentType environmentType = requestParameters.getEnvironmentType();
        String date = requestParameters.getLocalDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String city = requestParameters.getCity();

        Map<Object, Object> data = new HashMap<>();
        data.put("outdoors", environmentType == EnvironmentType.BOTH ? "" : (environmentType == EnvironmentType.OUTDOOR ? 0 : 1));
        data.put("sport", "5");
        data.put("date", date);
        data.put("q", URLDecoder.decode(city, StandardCharsets.UTF_8));

        return data;
    }
}
