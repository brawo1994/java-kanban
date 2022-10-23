package ru.yandex.kanban.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.kanban.service.managers.historyManagers.InMemoryHistoryManager;
import ru.yandex.kanban.tasks.Task;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class HistoryManagerTest {

    InMemoryHistoryManager historyManager;

    @BeforeEach
    void setup() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getEmptyHistory() {
        assertEquals(0, historyManager.getHistory().size());
    }

    @Test
    void addHistory() {
        Task task1 = new Task(1, "Title", "Description", 0, Instant.now());
        Task task2 = new Task(2, "Title", "Description", 0, Instant.now());
        Task task3 = new Task(3, "Title", "Description", 0, Instant.now());
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
        assertEquals(task3, historyManager.getHistory().get(2));
    }

    @Test
    void addHistoryWithManyAddTask() {
        Task task1 = new Task(1, "Title", "Description", 0, Instant.now());
        Task task2 = new Task(2, "Title", "Description", 0, Instant.now());
        Task task3 = new Task(3, "Title", "Description", 0, Instant.now());
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);

        assertEquals(3, historyManager.getHistory().size());
        assertEquals(task2, historyManager.getHistory().get(0));
        assertEquals(task3, historyManager.getHistory().get(1));
        assertEquals(task1, historyManager.getHistory().get(2));
    }

    @Test
    void addHistoryWithRemoveTask() {
        Task task1 = new Task(1, "Title", "Description", 0, Instant.now());
        Task task2 = new Task(2, "Title", "Description", 0, Instant.now());
        Task task3 = new Task(3, "Title", "Description", 0, Instant.now());
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        historyManager.add(task1);
        historyManager.add(task3);
        historyManager.add(task2);
        historyManager.remove(3);

        assertEquals(2, historyManager.getHistory().size());
        assertEquals(task1, historyManager.getHistory().get(0));
        assertEquals(task2, historyManager.getHistory().get(1));
        assertFalse(historyManager.getHistory().contains(task3));
    }

    @Test
    void duplicateTaskInHistory() {
        Task task = new Task(1, "Title", "Description", 0, Instant.now());
        historyManager.add(task);
        historyManager.add(task);
        historyManager.add(task);

        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }
}