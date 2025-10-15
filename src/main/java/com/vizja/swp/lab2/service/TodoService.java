package com.vizja.swp.lab2.service;

import com.vizja.swp.lab2.model.Todo;
import java.util.*;

public class TodoService {
    private final Map<Integer, Todo> todos = new LinkedHashMap<>();
    private int idCounter = 1;

    public List<Todo> getAll() {
        return new ArrayList<>(todos.values());
    }

    public Todo getById(int id) {
        return todos.get(id);
    }

    public Todo create(String title) {
        Todo todo = new Todo(idCounter++, title, false);
        todos.put(todo.getId(), todo);
        return todo;
    }

    public Todo update(int id, String title, boolean completed) {
        Todo todo = todos.get(id);
        if (todo != null) {
            todo.setTitle(title);
            todo.setCompleted(completed);
        }
        return todo;
    }

    public boolean delete(int id) {
        return todos.remove(id) != null;
    }
}
