package com.matchi.bargain.finder;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.http.HttpClient;

public final class CommonUtils {
    private CommonUtils() {
    }

    public static ObjectMapper MAPPER = new ObjectMapper();
    public static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Sleep interrupted... Resuming");
            e.printStackTrace();
        }
    }
}
