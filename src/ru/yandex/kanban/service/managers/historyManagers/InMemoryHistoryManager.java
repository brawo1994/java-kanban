package ru.yandex.kanban.service.managers.historyManagers;

import ru.yandex.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    @Override
    public void add(Task task) {
        removeNode(task.getId());
        linkLast(task);
        nodeMap.put(task.getId(), last);
    }

    @Override
    public void remove(int id) {
        removeNode(id);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void removeNode(Integer id) {
        Node node = nodeMap.remove(id);
        if (node == null) {
            // Задача с переданным ID задачи отсутствует в Истории
            return;
        }
        if (node.prev != null) {
            // Удаляемая нода не первая
            node.prev.next = node.next;
            if (node.next != null){
                // Удаляемая нода не последняя
                node.next.prev = node.prev;
            } else {
                // Удаляемая нода последняя
                last = node.prev;
            }
        } else {
            //Удаляемая нода первая
            first = node.next;
            if (first == null){
                // Удаляемая нода единственная
                last = null;
            } else {
                // Удаляемая нода НЕ единственная
                node.next.prev = null;
            }
        }
    }

    private void linkLast(Task task){
        Node node = new Node(task, last, null);
        if (first == null){
            // Список пустой
            first = node;
        } else {
            // Список НЕ пустой
            last.next = node;
        }
        last = node;
    }

    private List<Task> getTasks(){
        List<Task> historyTasksList = new ArrayList<>();
        Node currentNode = first;
        while (currentNode != null){
            historyTasksList.add(currentNode.task);
            currentNode = currentNode.next;
        }
        return historyTasksList;
    }
}
