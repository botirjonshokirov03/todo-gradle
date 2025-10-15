package com.vizja.swp.lab2.lib.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final HttpMethod method;
    private final String path;
    private final String version;
    private String query;
    private final Map<String, String> headers = new HashMap<>();
    private String body;

    public HttpRequest(HttpMethod method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static HttpRequest parse(String requestLine, BufferedReader reader) throws IOException {
        String[] parts = requestLine.split(" ");
        var method = HttpMethod.fromString(parts[0]);
        var fullPath = parts[1];
        var version = parts[2];

        // --- Extract path and query ---
        String path;
        String query = null;
        int queryIndex = fullPath.indexOf('?');
        if (queryIndex != -1) {
            path = fullPath.substring(0, queryIndex);
            query = fullPath.substring(queryIndex + 1);
        } else {
            path = fullPath;
        }

        var request = new HttpRequest(method, path, version);
        request.query = query; // ✅ Set query string here

        // --- Read headers ---
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            int idx = line.indexOf(":");
            if (idx > 0) {
                var key = line.substring(0, idx).trim();
                var value = line.substring(idx + 1).trim();
                request.headers.put(key, value);
            }
        }

        // --- Read body if Content-Length present ---
        if (request.headers.containsKey("Content-Length")) {
            int length = Integer.parseInt(request.headers.get("Content-Length"));
            char[] buf = new char[length];
            reader.read(buf, 0, length);
            request.body = new String(buf);
        }

        return request;
    }

    public String getBody() {
        return body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQuery() {
        return query;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Optional<String> getHeader(String name) {
        return Optional.ofNullable(headers.get(name));
    }
}
