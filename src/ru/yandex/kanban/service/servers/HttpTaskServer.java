package ru.yandex.kanban.service.servers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.yandex.kanban.service.managers.taskManagers.TaskManager;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class HttpTaskServer {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final int PORT = 8080;
    private final TaskManager taskManager;
    private final HttpServer server;
    private static Gson gson;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        gson = new Gson();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен на " + PORT + " порту!");
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange httpExchange) {

            String path = httpExchange.getRequestURI().getPath();
            String method = httpExchange.getRequestMethod();
            String query = httpExchange.getRequestURI().getQuery();
            String response = "";
            int responseCode = 404;

            try {
                switch (method) {
                    case "GET":
                        if (Pattern.matches("^/tasks$", path)) {
                            response = gson.toJson(taskManager.getPrioritizedTasks());
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/task$", path) && query != null) {
                            response = gson.toJson(taskManager.getTask(getTaskIdFromQuery(query)));
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/task$", path)) {
                            response = gson.toJson(taskManager.getAllTasks());
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask/epic$", path) && query != null) {
                            response = gson.toJson(taskManager.getAllSubTasksByEpic(getTaskIdFromQuery(query)));
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path) && query != null) {
                            response = gson.toJson(taskManager.getEpic(getTaskIdFromQuery(query)));
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            response = gson.toJson(taskManager.getAllEpics());
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path) && query != null) {
                            response = gson.toJson(taskManager.getSubTask(getTaskIdFromQuery(query)));
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            response = gson.toJson(taskManager.getAllSubTasks());
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/history$", path)) {
                            response = gson.toJson(taskManager.getHistory());
                            responseCode = 200;
                        }
                        break;

                    case "POST":
                        if (Pattern.matches("^/tasks/task$", path)){
                            InputStream inputStream = httpExchange.getRequestBody();
                            String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            Task task = gson.fromJson(taskBody, Task.class);
                            taskManager.addNewTask(task);
                            responseCode = 201;
                        }
                        if (Pattern.matches("^/tasks/epic$", path)){
                            InputStream inputStream = httpExchange.getRequestBody();
                            String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            Epic epic = gson.fromJson(taskBody, Epic.class);
                            taskManager.addNewEpic(epic);
                            responseCode = 201;
                        }
                        if (Pattern.matches("^/tasks/subtask$", path)){
                            InputStream inputStream = httpExchange.getRequestBody();
                            String taskBody = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
                            SubTask subTask = gson.fromJson(taskBody, SubTask.class);
                            taskManager.addNewSubTask(subTask);
                            responseCode = 201;
                        } else {
                            System.out.println("Метод " + method + ", но path не распознан " + path);
                        }
                        break;

                    case "DELETE":
                        if (Pattern.matches("^/tasks/task$", path) && query != null) {
                            int id = getTaskIdFromQuery(query);
                            taskManager.deleteTask(id);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/task$", path)) {
                            taskManager.deleteAllTasks();
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path) && query != null) {
                            int id = getTaskIdFromQuery(query);
                            taskManager.deleteEpic(id);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/epic$", path)) {
                            taskManager.deleteAllEpics();
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path) && query != null) {
                            int id = getTaskIdFromQuery(query);
                            taskManager.deleteSubTask(id);
                            responseCode = 200;
                        } else if (Pattern.matches("^/tasks/subtask$", path)) {
                            taskManager.deleteAllSubTask();
                            responseCode = 200;
                        }
                        break;
                    default:
                        System.out.println("Получен запрос с неожидаемым методом: " + method);
                }
                httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                httpExchange.sendResponseHeaders(responseCode, 0);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes(DEFAULT_CHARSET));
                }
            } catch (IOException e) {
                System.out.println("Ошибка выполнения запроса: " + e.getMessage());
            } finally {
                httpExchange.close();
            }
        }
    }

    private static int getTaskIdFromQuery(String query) {
        String[] queryArray = query.split("=");
        return Integer.parseInt(queryArray[1]);
    }
}
