package ru.yandex.kanban.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.service.managers.taskManagers.InMemoryTasksManager;
import ru.yandex.kanban.service.managers.taskManagers.TaskManager;
import ru.yandex.kanban.service.servers.HttpTaskServer;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpTasksServerTest{

    private HttpTaskServer HttpTaskServer;
    private Task task1;
    private Epic epic;
    private SubTask subTask;
    private Gson gson;

    @BeforeEach
    void setup() throws IOException {

        TaskManager taskManager = new InMemoryTasksManager();
        HttpTaskServer = new HttpTaskServer(taskManager);
        gson = new Gson();
        task1 = new Task(1,"Title1", "Description1", 0, Instant.now());
        Task task2 = new Task(2, "Title2", "Description2", 0, Instant.now());
        epic = new Epic(3,"Title4", "Description4");
        subTask = new SubTask(4,"Title5", "Description5", 3, 0, Instant.now());
        taskManager.addNewTask(task1);
        taskManager.addNewTask(task2);
        taskManager.addNewEpic(epic);
        taskManager.addNewSubTask(subTask);
        taskManager.getTask(task2.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic.getId());
        HttpTaskServer.start();
    }

    @AfterEach
    void serverStop() {
        HttpTaskServer.stop();
    }

    @Test
    void getEpicsWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Epic>>(){}.getType();
        List<Epic> epics = gson.fromJson(response.body(), type);

        assertNotNull(epics);
        assertEquals(1, epics.size());
    }

    @Test
    void getTasksWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    }

    @Test
    void getSubTasksWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<SubTask>>(){}.getType();
        List<SubTask> subTasks = gson.fromJson(response.body(), type);

        assertNotNull(subTasks);
        assertEquals(1, subTasks.size());
    }

    @Test
    void getTaskWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<Task>(){}.getType();
        Task task = gson.fromJson(response.body(), type);

        assertNotNull(task);
        assertEquals(task1, task);
    }

    @Test
    void getHistoryWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/history");
        HttpRequest request = HttpRequest.newBuilder().
                uri(url).
                GET().
                build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(3, tasks.size());
    }

    @Test
    void getPrioritizedTasksWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Type type = new TypeToken<List<Task>>(){}.getType();
        List<Task> tasks = gson.fromJson(response.body(), type);

        assertNotNull(tasks);
        assertEquals(3, tasks.size());
    }

    @Test
    void createTaskWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task");
        String json = gson.toJson(task1);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void createEpicWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/epic");
        String json = gson.toJson(epic);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void createSubTaskWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/subtask");
        String json = gson.toJson(subTask);
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(body)
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
    }

    @Test
    void deleteTaskWithApi() throws IOException, InterruptedException {

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/task?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
    }
}