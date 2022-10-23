package ru.yandex.kanban.service.managers.historyManagers;

import ru.yandex.kanban.tasks.Task;

public class Node {
    Task task;
    Node prev;
    Node next;

    public Node(Task task, Node prev, Node next) {
        this.task = task;
        this.prev = prev;
        this.next = next;
    }
}
