package com.vizja.swp.lab2.lib;

import com.vizja.swp.lab2.lib.http.HttpRequest;
import com.vizja.swp.lab2.lib.http.HttpResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FrontController {
    private static final Map<String, BaseController> routes = new ConcurrentHashMap<>();

    public static HttpResponse handle(HttpRequest request) {
        HttpResponse response = new HttpResponse();

        // Extract base path without query parameters
        String path = request.getPath();
        if (path.contains("?")) {
            path = path.substring(0, path.indexOf("?"));
        }

        // Try to match path directly, or prefix for things like /todos/1
        BaseController controller = routes.get(path);
        if (controller == null) {
            // Check prefix match for REST-style routes
            for (Map.Entry<String, BaseController> entry : routes.entrySet()) {
                if (path.startsWith(entry.getKey())) {
                    controller = entry.getValue();
                    break;
                }
            }
        }

        if (controller == null) {
            response.setStatus(404, "Not Found");
            response.getWriter().println("404 Not Found: " + request.getPath());
            return response;
        }

        try {
            controller.handle(request, response);
        } catch (UnsupportedOperationException e) {
            response.setStatus(405, "Method Not Allowed");
            response.getWriter().println(e.getMessage());
        } catch (Exception e) {
            response.setStatus(500, "Internal Server Error");
            response.getWriter().println("Server error: " + e.getMessage());
        }

        return response;
    }

    public static Map<String, BaseController> addRoute(String path, BaseController controller) {
        routes.put(path, controller);
        return routes;
    }
}
