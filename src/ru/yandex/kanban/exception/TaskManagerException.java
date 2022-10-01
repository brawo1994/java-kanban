package ru.yandex.kanban.exception;

public class TaskManagerException extends RuntimeException {
    public TaskManagerException(String message) {
        super(message);
    }
}