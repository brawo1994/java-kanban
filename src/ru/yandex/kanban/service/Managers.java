package ru.yandex.kanban.service;

import java.io.File;

public class Managers {
    public static TaskManager getDefaultTaskManager() {
        return new FileBackedTasksManager(new File("src/ru/yandex/kanban/history.csv"));
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}