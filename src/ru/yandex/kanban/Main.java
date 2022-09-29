package ru.yandex.kanban;

import ru.yandex.kanban.service.FileBackedTasksManager;
import ru.yandex.kanban.service.Managers;
import ru.yandex.kanban.service.TaskManager;

import java.io.File;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        int taskId1 = taskManager.addNewTask("Задача 1", "Описание задачи 1");
        int taskId2 = taskManager.addNewTask("Задача 2", "Описание задачи 2");
        int epicId1 = taskManager.addNewEpic("Эпик 1", "Описание эпика 1");
        int epicId2 = taskManager.addNewEpic("Эпик 2", "Описание эпика 2");
        int subTask1 = taskManager.addNewSubTask("Подзадача 1", "Описание подзадачи 1", epicId1);
        int subTask2 = taskManager.addNewSubTask("Подзадача 2", "Описание подзадачи 2", epicId1);
        int subTask3 = taskManager.addNewSubTask("Подзадача 3", "Описание подзадачи 3", epicId1);

        System.out.println(taskManager.getAllSubTasks());
        System.out.println(taskManager.getAllEpics());
        System.out.println(taskManager.getAllTasks());
        System.out.println();
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();
        TaskManager taskManager2 = FileBackedTasksManager.loadFromFile(new File("src/ru/yandex/kanban/history.csv"));
        System.out.println("Создал taskManager восстановленный из файла");
        System.out.println();
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager2.getHistory());
        System.out.println();
        System.out.println(taskManager2.getAllSubTasks());
        System.out.println(taskManager2.getAllEpics());
        System.out.println(taskManager2.getAllTasks());
    }
}
