package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void setup() {
        taskManager = new FileBackedTasksManager();
    }

    @Test
    void loadedFromFileTasksManager() {
        final String filePath = "src/ru/yandex/kanban/history.csv";
        final File file = new File(filePath);
        Task task1 = createTask();
        Task task2 = createTask();
        Epic epic1 = createEpic();
        Epic epic2 = createEpic();
        SubTask subTask1 = createSubTask(epic1);
        SubTask subTask2 = createSubTask(epic1);
        SubTask subTask3 = createSubTask(epic2);
        SubTask subTask4 = createSubTask(epic2);
        taskManager.getTask(task2.getId());
        taskManager.getSubTask(subTask4.getId());
        taskManager.getEpic(epic1.getId());
        List<Task> history = taskManager.getHistory();

        FileBackedTasksManager taskManagerFromFile = FileBackedTasksManager.loadFromFile(file);
        List<Task> historyFromFile = taskManagerFromFile.getHistory();
        List<Task> allTasksFromFile = taskManagerFromFile.getAllTasks();
        List<Epic> allEpicsFromFile = taskManagerFromFile.getAllEpics();
        List<SubTask> allSubTasksFromFile = taskManagerFromFile.getAllSubTasks();
        List<Task> allTasks = taskManager.getAllTasks();
        List<Epic> allEpics = taskManager.getAllEpics();
        List<SubTask> allSubTasks = taskManager.getAllSubTasks();

        assertEquals(history, historyFromFile);
        assertEquals(allTasks, allTasksFromFile);
        assertEquals(allEpics, allEpicsFromFile);
        assertEquals(allSubTasks, allSubTasksFromFile);
    }
}