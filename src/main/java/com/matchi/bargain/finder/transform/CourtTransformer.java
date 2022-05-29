package com.matchi.bargain.finder.transform;

import com.fasterxml.jackson.core.type.TypeReference;
import com.matchi.bargain.finder.model.Court;
import com.matchi.bargain.finder.model.Slot;
import com.matchi.bargain.finder.model.TransformData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

import static com.matchi.bargain.finder.CommonUtils.HTTP_CLIENT;
import static com.matchi.bargain.finder.CommonUtils.MAPPER;

public class CourtTransformer implements Transformer<TransformData, List<Court>> {

    public static final CourtTransformer INSTANCE = new CourtTransformer();

    private CourtTransformer() {
    }


    @Override
    public List<Court> transform(TransformData transformData) {
        Document document = Jsoup.parse(transformData.getHtml());
        Elements facilities = document.getElementsByClass("row");
        String timeFilterString = getTimeFilterString(transformData.getHour(), transformData.getMinute());
        Map<String, Court> slotPriceCourtMap = facilities.stream()
                .flatMap(element -> {
                    try {
                        String facilityName = element.getElementsByClass("media-heading h4").get(0).getAllElements().get(1).childNodes().get(0).toString();
                        return getCourts(element, facilityName, timeFilterString).stream();
                    } catch (RuntimeException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Court::getSlotPriceId, court -> court));

        if(!slotPriceCourtMap.isEmpty()) {
            List<Slot> slotPrices = getSlotPrices(new ArrayList<>(slotPriceCourtMap.keySet()));

            slotPrices.stream()
                    .forEach(slot -> {
                        int price = slot.getPrice();
                        Court court = slotPriceCourtMap.get(slot.getSlotId());
                        court.setPrice(price);
                        if(court.getDuration().contains("60")) {
                            court.setHourlyRate(price);
                        } else {
                            court.setHourlyRate(price / 1.5);
                        }
                    });
            return new ArrayList<>(slotPriceCourtMap.values());
        }
        return Collections.emptyList();
    }

    private List<Court> getCourts(Element element, String facilityName, String timeToFilter) {
        return element.getElementsByClass("list-group alt").stream()
                .filter(e -> e.getElementsByTag("strong").get(1).toString().equals(timeToFilter))
                .flatMap(e -> e.getElementsByClass("list-group-item").stream())
                .filter(e -> e.getAllElements().size() > 5) //Filter out first list-group-item in list-group-alt
                .map(e -> {
                    //Element 4 name of court
                    String courtName = e.getAllElements().get(4).getAllElements().get(0).childNodes().get(0).toString();
                    //Element 5 duration
                    String duration = e.getAllElements().get(5).getAllElements().get(0).childNodes().get(0).toString();
                    //Element 13 price
                    String slotPriceId = e.getAllElements().get(13).getAllElements().get(0).attributes().toString();
                    slotPriceId = slotPriceId.substring(11, slotPriceId.length() - 1);
                    //Temporary solution to retrieve price here
                    //int price = getSlotPrice(slotPriceId);
                    return new Court(courtName, duration, facilityName, slotPriceId);
                })
                .collect(Collectors.toList());
    }

    private List<Slot> getSlotPrices(List<String> slotIds) {
        String uri = String.format("https://www.matchi.se/book/getSlotPrices?slotId=%s", slotIds.remove(0));
        if(!slotIds.isEmpty()) {
            String queryParams = "&slotId=";
            queryParams += String.join("&slotId=", slotIds);
            uri += queryParams;
        }

        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            List<Slot> slots = MAPPER.readValue(response.body(), new TypeReference<>() {
            });
            return slots;
        } catch (IOException | InterruptedException e) {
            if(e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new RuntimeException("Could not retrieve slot price", e);
        }
    }

    private String getTimeFilterString(String hour, String minute) {
        return String.format("<strong>%s<sup>%s</sup></strong>", hour, minute);
    }
}
