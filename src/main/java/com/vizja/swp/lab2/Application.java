package com.vizja.swp.lab2;

import com.vizja.swp.lab2.controller.HomeController;
import com.vizja.swp.lab2.controller.TodoController;
import com.vizja.swp.lab2.lib.FrontController;
import com.vizja.swp.lab2.lib.Server;

public class Application {
    public static void main(String[] args) {

        // ✅ Create controllers
        HomeController homeController = new HomeController();
        TodoController todoController = new TodoController();

        // ✅ Register routes
        FrontController.addRoute("/", homeController);
        FrontController.addRoute("/todos", todoController);
        FrontController.addRoute("/todos/edit", todoController); // handled in same controller

        // ✅ Start the server
        try (final var server = new Server()) {
            server.start(8080);
        }
    }
}
