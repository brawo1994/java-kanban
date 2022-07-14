package ru.yandex.kanban.service;

import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.util.ArrayList;

public interface TaskManager {

    // Методы для TASK
    int addNewTask(String name, String description);

    void updateTask(Task task);

    void deleteAllTasks();

    void deleteTask(int taskId);

    Task getTask(int taskId);

    ArrayList<Task> getAllTasks();

    // Методы EPIC
    int addNewEpic(String name, String description);

    void updateEpic(Epic epic);

    void deleteAllEpics();

    void deleteEpic(int epicId);

    Epic getEpic(int epicId);

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubTasksByEpic(int epicId);

    // Методы SUBTASK
    int addNewSubTask(String name, String description, int epicId);

    void updateSubTask(SubTask subTask);

    void deleteAllSubTask();

    void deleteSubTask(int subTaskId);

    SubTask getSubTask(int subTaskId);

    ArrayList<SubTask> getAllSubTasks();
}
