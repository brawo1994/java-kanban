package ru.yandex.kanban.service;

import ru.yandex.kanban.tasks.Task;
import java.util.List;

public interface HistoryManager {
    void add(Task task);

    List<Task> getHistory();
}