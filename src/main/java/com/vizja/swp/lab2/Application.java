package com.vizja.swp.lab2;


import com.vizja.swp.lab2.lib.Server;

public class Application {
    public static void main(String[] args) {

        try (final var server = new Server()) {

            server.start(8080);
        }
    }
}