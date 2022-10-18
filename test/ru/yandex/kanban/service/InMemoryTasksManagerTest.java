package ru.yandex.kanban.service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTasksManagerTest extends TaskManagerTest<InMemoryTasksManager> {

    @BeforeEach
    void setup() {
        taskManager = new InMemoryTasksManager();
    }
}