package ru.yandex.kanban.service;

public class Managers {
    public static TaskManager getDefaultTasksManager() {
        return new InMemoryTasksManager();
    }

    public static TaskManager getDefaultBackedTasksManager() {
        return new FileBackedTasksManager();
    }

    public static HistoryManager getDefaultHistoryManager() {
        return new InMemoryHistoryManager();
    }
}