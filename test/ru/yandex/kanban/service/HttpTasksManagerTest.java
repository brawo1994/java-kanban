package ru.yandex.kanban.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.service.managers.taskManagers.HTTPTaskManager;
import ru.yandex.kanban.service.servers.KVServer;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTasksManagerTest extends TaskManagerTest<HTTPTaskManager> {

    protected KVServer KVServer;

    @BeforeEach
    public void setup() throws IOException {
        KVServer = new KVServer();
        KVServer.start();
        taskManager = new HTTPTaskManager();
    }

    @AfterEach
    void serverStop() {
        KVServer.stop();
    }

    @Test
    void loadFromServer() {
        createTask();
        Task task = createTask();
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        createSubTask(epic1);
        createSubTask(epic1);
        createSubTask(epic2);
        SubTask subTask = createSubTask(epic2);
        taskManager.getTask(task.getId());
        taskManager.getSubTask(subTask.getId());
        taskManager.getEpic(epic1.getId());
        List<Task> allTasks = taskManager.getAllTasks();
        List<Epic> allEpics = taskManager.getAllEpics();
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();
        List<Task> history = taskManager.getHistory();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        HTTPTaskManager taskManagerFromKv = new HTTPTaskManager();
        taskManagerFromKv.load();

        List<Task> historyFromKV = taskManagerFromKv.getHistory();
        List<Task> prioritizedTasksFromKV = taskManagerFromKv.getPrioritizedTasks();
        List<Task> allTasksFromKV = taskManagerFromKv.getAllTasks();
        List<Epic> allEpicsFromKV = taskManagerFromKv.getAllEpics();
        List<SubTask> allSubTasksFromKV = taskManagerFromKv.getAllSubTasks();

        assertEquals(history, historyFromKV);
        assertEquals(prioritizedTasks, prioritizedTasksFromKV);
        assertEquals(allTasks, allTasksFromKV);
        assertEquals(allEpics, allEpicsFromKV);
        assertEquals(allSubTasks, allSubTasksFromKV);
    }
}
