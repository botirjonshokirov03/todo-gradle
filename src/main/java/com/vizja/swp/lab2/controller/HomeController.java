package com.vizja.swp.lab2.controller;

import com.vizja.swp.lab2.lib.BaseController;
import com.vizja.swp.lab2.lib.http.HttpRequest;
import com.vizja.swp.lab2.lib.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class HomeController extends BaseController {

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        try (InputStream input = getClass().getResourceAsStream("/templates/index.html")) {
            if (input == null) {
                response.setStatus(404, "Not Found");
                response.getWriter().println("index.html not found");
                return;
            }

            String html = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            response.setStatus(200, "OK");
            response.setHeader("Content-Type", "text/html; charset=UTF-8");
            response.getWriter().println(html);

        } catch (IOException e) {
            response.setStatus(500, "Internal Server Error");
            response.getWriter().println("Error reading index.html: " + e.getMessage());
        }
    }
}
