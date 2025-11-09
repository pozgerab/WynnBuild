package com.gertoxq.wynnbuild.webquery;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class HttpHelper {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .executor(Executors.newFixedThreadPool(4))
            .build();

    public static CompletableFuture<HttpResponse<String>> get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        return CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
