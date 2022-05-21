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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
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
        return facilities.stream()
                .flatMap(element -> {
                    try {
                        String facilityName = element.getElementsByClass("media-heading h4").get(0).getAllElements().get(1).childNodes().get(0).toString();
                        return getCourts(element, facilityName, timeFilterString).stream();
                    } catch (RuntimeException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
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
                    String priceSlotId = e.getAllElements().get(13).getAllElements().get(0).attributes().toString();
                    priceSlotId = priceSlotId.substring(11, priceSlotId.length() - 1);
                    //Temporary solution to retrieve price here
                    int price = getSlotPrice(priceSlotId);
                    if(duration.contains("60")) {
                        return new Court(courtName, price, duration, facilityName, price);
                    } else {
                        return new Court(courtName, price, duration, facilityName, price / 1.5);
                    }
                })
                .collect(Collectors.toList());
    }

    private int getSlotPrice(String slotId) {
        String uri = String.format("https://www.matchi.se/book/getSlotPrices?slotId=%s", slotId);
        HttpRequest request = HttpRequest.newBuilder(URI.create(uri))
                .GET()
                .build();

        try {
            HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            List<Slot> slots = MAPPER.readValue(response.body(), new TypeReference<>() {
            });
            return slots.get(0).getPrice();
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
