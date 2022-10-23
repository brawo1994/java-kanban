package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.kanban.service.managers.taskManagers.InMemoryTasksManager;

class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTasksManager> {

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTasksManager();
    }
}