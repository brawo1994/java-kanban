package ru.yandex.kanban;
import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.service.HistoryManager;
import ru.yandex.kanban.service.Managers;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

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

        taskManager.getTask(taskId1);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getEpic(epicId1);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getSubTask(subTask1);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getTask(taskId2);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getSubTask(subTask3);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getEpic(epicId2);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.getSubTask(subTask1);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.deleteTask(taskId1);
        System.out.println("==========Удалили задачу 1==========");
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        taskManager.deleteEpic(epicId1);
        System.out.println("==========Удалили Эпик 1==========");
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();
    }
}
