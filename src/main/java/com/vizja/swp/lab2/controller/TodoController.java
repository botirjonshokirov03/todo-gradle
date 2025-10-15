package com.vizja.swp.lab2.controller;

import com.vizja.swp.lab2.lib.BaseController;
import com.vizja.swp.lab2.lib.http.HttpRequest;
import com.vizja.swp.lab2.lib.http.HttpResponse;
import com.vizja.swp.lab2.model.Todo;
import com.vizja.swp.lab2.service.TodoService;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodoController extends BaseController {
    private final TodoService service = new TodoService();

    @Override
    public void doGet(HttpRequest request, HttpResponse response) {
        String path = request.getPath();

        if (path.startsWith("/todos/edit")) {
            showEditPage(request, response);
            return;
        }

        List<Todo> todos = service.getAll();
        PrintWriter out = response.getWriter();

        out.println("<html><head><title>Todo App</title></head><body>");
        out.println("<h1>Todo List</h1>");
        out.println("<form method='POST' action='/todos'>");
        out.println("<input name='title' placeholder='New todo' required>");
        out.println("<button type='submit'>Add</button>");
        out.println("</form><hr/>");

        out.println("<ul>");
        for (Todo t : todos) {
            out.printf(
                    "<li>%s %s "
                            + "<form style='display:inline' method='POST' action='/todos?id=%d'>"
                            + "<input type='hidden' name='_method' value='DELETE'/>"
                            + "<button type='submit'>Delete</button>"
                            + "</form> "
                            + "<a href='/todos/edit?id=%d'>Edit</a>"
                            + "</li>",
                    escapeHtml(t.getTitle()),
                    t.isCompleted() ? "✅" : "",
                    t.getId(),
                    t.getId()
            );
        }
        out.println("</ul>");
        out.println("</body></html>");
    }

    @Override
    public void doPost(HttpRequest request, HttpResponse response) {
        String body = request.getBody();
        if (body == null || body.isBlank()) {
            response.setStatus(400, "Bad Request");
            response.getWriter().println("Request body is empty");
            return;
        }

        String decoded = URLDecoder.decode(body, StandardCharsets.UTF_8);
        Map<String, String> params = parseFormData(decoded);
        String methodOverride = params.get("_method");

        // ----- DELETE -----
        if ("DELETE".equalsIgnoreCase(methodOverride)) {
            Integer id = extractIdFromQuery(request);
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.getWriter().println("Missing or invalid ID parameter");
                return;
            }

            boolean ok = service.delete(id);
            if (!ok) {
                response.setStatus(404, "Not Found");
                response.getWriter().println("Todo not found");
                return;
            }
        }
        // ----- UPDATE -----
        else if ("PUT".equalsIgnoreCase(methodOverride)) {
            Integer id = extractIdFromQuery(request);
            if (id == null) {
                response.setStatus(400, "Bad Request");
                response.getWriter().println("Missing ID parameter");
                return;
            }

            String title = params.get("title");
            boolean completed = params.containsKey("completed");

            if (title == null || title.isBlank()) {
                response.setStatus(400, "Bad Request");
                response.getWriter().println("Missing title field");
                return;
            }

            Todo updated = service.update(id, title.trim(), completed);
            if (updated == null) {
                response.setStatus(404, "Not Found");
                response.getWriter().println("Todo not found");
                return;
            }
        }
        // ----- CREATE -----
        else {
            String title = params.get("title");
            if (title == null || title.isBlank()) {
                response.setStatus(400, "Bad Request");
                response.getWriter().println("Missing title field");
                return;
            }

            service.create(title.trim());
        }

        // Redirect back to list
        response.setHeader("Location", "/todos");
        response.setStatus(302, "Found");
    }

    private void showEditPage(HttpRequest request, HttpResponse response) {
        Integer id = extractIdFromQuery(request);
        if (id == null) {
            response.setStatus(400, "Bad Request");
            response.getWriter().println("Missing ID parameter");
            return;
        }

        Todo todo = service.getById(id);
        if (todo == null) {
            response.setStatus(404, "Not Found");
            response.getWriter().println("Todo not found");
            return;
        }

        PrintWriter out = response.getWriter();
        out.println("<html><head><title>Edit Todo</title></head><body>");
        out.printf("<h1>Edit Todo #%d</h1>", todo.getId());
        out.printf(
                "<form method='POST' action='/todos?id=%d'>"
                        + "<input type='hidden' name='_method' value='PUT'/>"
                        + "<input name='title' value='%s' required/>"
                        + "<label><input type='checkbox' name='completed' %s> Completed</label><br><br>"
                        + "<button type='submit'>Save</button>"
                        + "</form>"
                        + "<a href='/todos'>Cancel</a>",
                todo.getId(),
                escapeHtml(todo.getTitle()),
                todo.isCompleted() ? "checked" : ""
        );
        out.println("</body></html>");
    }

    // --- Helpers ---
    private static Map<String, String> parseFormData(String data) {
        Map<String, String> map = new HashMap<>();
        if (data == null || data.isBlank()) return map;
        for (String pair : data.split("&")) {
            if (pair.contains("=")) {
                String[] kv = pair.split("=", 2);
                map.put(kv[0], kv.length > 1 ? kv[1] : "");
            } else {
                map.put(pair, "on");
            }
        }
        return map;
    }

    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }

    private static Integer extractIdFromQuery(HttpRequest request) {
        String query = request.getQuery();
        String path = request.getPath();
        
        // Log for debugging
        System.out.println("Path: " + path);
        System.out.println("Query: " + query);

        // Fallback: manually extract from path
        if ((query == null || query.isBlank()) && path.contains("?")) {
            query = path.substring(path.indexOf('?') + 1);
            System.out.println("Fallback Query: " + query);
        }

        if (query == null || query.isBlank()) {
            System.out.println("No query string found");
            return null;
        }

        Map<String, String> params = parseFormData(query);
        System.out.println("Parsed Params: " + params);

        try {
            return params.containsKey("id") ? Integer.parseInt(params.get("id")) : null;
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format: " + params.get("id"));
            return null;
        }
    }
}