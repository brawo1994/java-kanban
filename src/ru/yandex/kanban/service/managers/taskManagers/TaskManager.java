package ru.yandex.kanban.service.managers.taskManagers;

import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.time.Instant;
import java.util.List;

public interface TaskManager {

    // Proxy для History
    List<Task> getHistory();

    // Методы для TASK
    int addNewTask(String name, String description, long duration, Instant startTime);

    int addNewTask(Task task);

    void updateTask(Task task);

    void deleteAllTasks();

    void deleteTask(int taskId);

    Task getTask(int taskId);

    List<Task> getAllTasks();

    // Методы EPIC
    int addNewEpic(String name, String description);

    int addNewEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteAllEpics();

    void deleteEpic(int epicId);

    Epic getEpic(int epicId);

    List<Epic> getAllEpics();

    List<SubTask> getAllSubTasksByEpic(int epicId);

    // Методы SUBTASK
    int addNewSubTask(String name, String description, int epicId, long duration, Instant startTime);

    int addNewSubTask(SubTask subTask);

    void updateSubTask(SubTask subTask);

    void deleteAllSubTask();

    void deleteSubTask(int subTaskId);

    SubTask getSubTask(int subTaskId);

    List<SubTask> getAllSubTasks();

    List<Task> getPrioritizedTasks();
}
