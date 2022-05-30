package com.matchi.bargain.finder;

import com.matchi.bargain.finder.model.Court;
import com.matchi.bargain.finder.model.TransformData;
import com.matchi.bargain.finder.transform.CourtTransformer;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.matchi.bargain.finder.CommonUtils.HTTP_CLIENT;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        Map<Object, Object> data = new HashMap<>();
        data.put("outdoors", "1");
        data.put("sport", "5");
        data.put("date", "2022-05-31");
        data.put("q", "Göteborg");

        HttpRequest request = HttpRequest.newBuilder(URI.create("https://www.matchi.se/book/findFacilities"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(ofFormData(data))
                .build();

        List<Court> courts = new ArrayList<>();
        int offset = 0;

        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = response.body();

        while(responseBody.length() > 2000) {
            TransformData transformData = new TransformData(responseBody, "18", "00");
            courts.addAll(CourtTransformer.INSTANCE.transform(transformData));

            offset += 10;
            request = getHttpRequest(offset, data);
            responseBody = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
        }

        courts.stream()
                .sorted(Comparator.comparing(Court::getHourlyRate))
                .forEach(court -> {
                    System.out.println();
                    System.out.println("Facility: " + court.getFacilityName());
                    System.out.println("Duration: " + court.getDuration());
                    System.out.println("Court: " + court.getName());
                    System.out.println("Price: " + court.getPrice());
                    System.out.println("Rate: " + court.getHourlyRate());
                });
    }

    private static HttpRequest getHttpRequest(int offset, Map<Object, Object> data) {
        data.put("offset", offset);
        return HttpRequest.newBuilder(URI.create("https://www.matchi.se/book/findFacilities"))
                .header("Accept", "*/*")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .POST(ofFormData(data))
                .build();
    }

    public static HttpRequest.BodyPublisher ofFormData(Map<Object, Object> data) {
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
}
