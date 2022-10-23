package ru.yandex.kanban.service.managers;

import ru.yandex.kanban.service.managers.historyManagers.HistoryManager;
import ru.yandex.kanban.service.managers.historyManagers.InMemoryHistoryManager;
import ru.yandex.kanban.service.managers.taskManagers.FileBackedTasksManager;
import ru.yandex.kanban.service.managers.taskManagers.HTTPTaskManager;
import ru.yandex.kanban.service.managers.taskManagers.InMemoryTasksManager;
import ru.yandex.kanban.service.managers.taskManagers.TaskManager;

public class Managers {
    public static TaskManager getDefaultTasksManager() {
        return new HTTPTaskManager();
    }

    public static TaskManager getDefaultBackedTasksManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}