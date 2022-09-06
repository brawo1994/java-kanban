package ru.yandex.kanban.service;

import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    protected int lastTaskId = 0;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();

    protected int getNewTaskId() {
        return ++lastTaskId;
    }

    // Proxy для запроса History
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Методы для TASK
    @Override
    public int addNewTask(String name, String description) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return 0;
        }
        Task task = new Task(getNewTaskId(), name, description);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Ошибка! Задача с идентификатором " + task.getId() + " не найдена!");
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
        }
        tasks.remove(taskId);
    }

    @Override
    public Task getTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
            return null;
        }
        historyManager.add(tasks.get(taskId));
        return tasks.get(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        for (int taskId : tasks.keySet()) {
            historyManager.add(tasks.get(taskId));
        }
        return new ArrayList<>(tasks.values());
    }

    // Методы EPIC
    @Override
    public int addNewEpic(String name, String description) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return 0;
        }
        Epic epic = new Epic(getNewTaskId(), name, description);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId())) {
            System.out.println("Ошибка! Эпик с идентификатором " + epic.getId() + " не найден!");
            return;
        }
        if (epics.get(epic.getId()).getStatus() != epic.getStatus()) {
            System.out.println("Ошибка! Самостоятельная смена статуса у Эпика запрещена!");
            return;
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            if (!epic.getSubTasksList().isEmpty()) {
                System.out.println("Ошибка! Невозможно удалить Эпик c id:" + epic.getId() + ", так как у него присутствуют подзадачи");
            } else {
                epics.remove(epic.getId());
            }
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
            return;
        }
        if (!epics.get(epicId).getSubTasksList().isEmpty()) {
            System.out.println("Ошибка! Невозможно удалить Эпик с подзадачами");
            return;
        }
        epics.remove(epicId);
    }

    @Override
    public Epic getEpic(int epicId) {
        if (!epics.containsKey(epicId)) System.out.println("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        historyManager.add(epics.get(epicId));
        return epics.get(epicId);
    }

    @Override
    public List<Epic> getAllEpics() {
        for (int epicId : epics.keySet()) {
            historyManager.add(tasks.get(epicId));
        }
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<SubTask> getAllSubTasksByEpic(int epicId) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTasksList()) {
            subTaskList.add(subTasks.get(subTaskId));
        }
        return subTaskList;
    }

    // Методы SUBTASK
    @Override
    public int addNewSubTask(String name, String description, int epicId) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return 0;
        }
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
            return 0;
        }
        SubTask subTask = new SubTask(getNewTaskId(), name, description, epicId);
        subTasks.put(subTask.getId(), subTask); //Добавляем Подзадачу
        epics.get(epicId).addSubTask(subTask.getId()); //Добавляем идентификатор подзадачи в Эпик, к которому привязываем подзадачу
        if (epics.get(epicId).getStatus() == TaskStatus.taskStatus.DONE) {
            calculateEpicStatus(epicId); //Если статус Эпика DONE, то нужно запустить перерасчет статуса Эпика
        }
        return subTask.getId();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTask.getId() + "не найдена!");
            return;
        }
        TaskStatus.taskStatus oldSubTaskStatus = subTasks.get(subTask.getId()).getStatus();
        subTasks.put(subTask.getId(), subTask);
        if (oldSubTaskStatus != subTask.getStatus()) {
            calculateEpicStatus(subTask.getEpicId()); //Если статус Подзадачи изменился, то нужно запустить перерасчет статуса Эпика
        }
    }

    @Override
    public void deleteAllSubTask() {
        //Запускаем в цикле удаление Подзадач по одной, так как нужно пересчитывать статус Эпика
        for (SubTask subTask : subTasks.values()) {
            deleteSubTask(subTask.getId());
        }
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        }
        int tempEpicId = subTasks.get(subTaskId).getEpicId();
        epics.get(tempEpicId).deleteSubTask(subTaskId); //Удаляем идентификатор удаляемой Подзадачи из Эпика
        calculateEpicStatus(tempEpicId); //Запускаем перерасчет статуса Эпика у Эпика удаляемой Подзадачи
        subTasks.remove(subTaskId); //Удаляем саму Подзадачу
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        }
        historyManager.add(subTasks.get(subTaskId));
        return subTasks.get(subTaskId);
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        for (int subTaskId : subTasks.keySet()){
            historyManager.add(subTasks.get(subTaskId));
        }
        return new ArrayList<>(subTasks.values());
    }

    protected void calculateEpicStatus(int epicId) {
        HashMap<TaskStatus.taskStatus, Integer> subTasksStatus = new HashMap<>();
        subTasksStatus.put(TaskStatus.taskStatus.NEW, 0);
        subTasksStatus.put(TaskStatus.taskStatus.IN_PROGRESS, 0);
        subTasksStatus.put(TaskStatus.taskStatus.DONE, 0);
        int subTasksCountInEpic = epics.get(epicId).getSubTasksList().size();
        for (Integer subTaskId : epics.get(epicId).getSubTasksList()) {
            SubTask subTask = subTasks.get(subTaskId);
            int tempCount = 0;
            if (subTasksStatus.containsKey(subTask.getStatus())) {
                tempCount = subTasksStatus.get(subTask.getStatus());
            }
            subTasksStatus.put(subTask.getStatus(), ++tempCount);
        }
        if (subTasksCountInEpic == 0 || subTasksCountInEpic == subTasksStatus.get(TaskStatus.taskStatus.NEW)) {
            epics.get(epicId).setStatus(TaskStatus.taskStatus.NEW);
            return;
        }
        if (subTasksCountInEpic == subTasksStatus.get(TaskStatus.taskStatus.DONE)) {
            epics.get(epicId).setStatus(TaskStatus.taskStatus.DONE);
            return;
        }
        if (epics.get(epicId).getStatus() != TaskStatus.taskStatus.IN_PROGRESS) {
            epics.get(epicId).setStatus(TaskStatus.taskStatus.IN_PROGRESS);
        }
    }
}
