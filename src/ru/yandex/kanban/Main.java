package ru.yandex.kanban;

import ru.yandex.kanban.service.managers.Managers;
import ru.yandex.kanban.service.managers.taskManagers.HTTPTaskManager;
import ru.yandex.kanban.service.managers.taskManagers.TaskManager;
import ru.yandex.kanban.service.servers.HttpTaskServer;
import ru.yandex.kanban.service.servers.KVServer;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {
        new KVServer().start();

        TaskManager taskManager = Managers.getDefaultTasksManager();
        new HttpTaskServer(taskManager).start();

        taskManager.addNewTask("Задача 1", "Описание задачи 1", 0, Instant.now());
        taskManager.addNewTask("Задача 2", "Описание задачи 2", 0, Instant.now());
        int epicId1 = taskManager.addNewEpic("Эпик 1", "Описание эпика 1");
        int epicId2 = taskManager.addNewEpic("Эпик 2", "Описание эпика 2");
        taskManager.addNewSubTask("Подзадача 1", "Описание подзадачи 1", epicId1, 0, Instant.now());
        taskManager.addNewSubTask("Подзадача 2", "Описание подзадачи 2", epicId1, 0, Instant.now());
        taskManager.addNewSubTask("Подзадача 3", "Описание подзадачи 3", epicId2, 0, Instant.now());

        List<Task> tasks1 = taskManager.getAllTasks();
        List<SubTask> subTasks1 = taskManager.getAllSubTasks();
        List<Epic> epics1 = taskManager.getAllEpics();
        List<Task> history1 = taskManager.getHistory();
        List<Task> prioritizedTasks1 = taskManager.getPrioritizedTasks();
        System.out.println(history1);
        System.out.println(tasks1);
        System.out.println(subTasks1);
        System.out.println(epics1);
        System.out.println(prioritizedTasks1);
        System.out.println();
        System.out.println();
        HTTPTaskManager taskManager2 = new HTTPTaskManager();
        taskManager2.load();
        System.out.println("Создал HTTPTaskManager, восстановленный из KV");

        List<Task> history2 = taskManager2.getHistory();
        List<Task> tasks2 = taskManager2.getAllTasks();
        List<SubTask> subTasks2 = taskManager2.getAllSubTasks();
        List<Epic> epics2 = taskManager2.getAllEpics();
        List<Task> prioritizedTasks2 = taskManager2.getPrioritizedTasks();
        System.out.println(history2);
        System.out.println(tasks2);
        System.out.println(subTasks2);
        System.out.println(epics2);
        System.out.println(prioritizedTasks2);

        if (history1.equals(history2)){
            System.out.println("Истории просмотров двух taskManagers равны");
        } else {
            System.out.println("Истории просмотров двух taskManagers НЕ равны");
        }
        if (tasks1.equals(tasks2)){
            System.out.println("Списки задач в двух taskManagers одинаковы");
        } else {
            System.out.println("Списки задач в двух taskManagers разные");
        }
        if (subTasks1.equals(subTasks2)){
            System.out.println("Списки подзадач в двух taskManagers одинаковы");
        } else {
            System.out.println("Списки подзадач в двух taskManagers разные");
        }
        if (epics1.equals(epics2)){
            System.out.println("Списки эпиков в двух taskManagers одинаковы");
        } else {
            System.out.println("Списки эпиков в двух taskManagers разные");
        }
        if (prioritizedTasks1.equals(prioritizedTasks2)){
            System.out.println("Списки задач по приоритету в двух taskManagers одинаковые");
        } else {
            System.out.println("Списки задач по приоритету в двух taskManagers разные");
        }
    }
}