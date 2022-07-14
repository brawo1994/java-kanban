package ru.yandex.kanban.service;

import ru.yandex.kanban.tasks.Task;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    protected static LinkedList<Task> history = new LinkedList<>();
    protected final static int HISTORY_SIZE = 10;

    @Override
    public void add(Task task) {
        if (history.size() >= HISTORY_SIZE){
            history.removeLast();
        }
        history.add(0,task);
    }

    @Override
    public List<Task> getHistory() {
        return history;
    }
}
