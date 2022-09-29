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
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
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
        if (name.isEmpty() || description.isEmpty())
            throw new RuntimeException("Ошибка! Название или описание пустое");
        Task task = new Task(getNewTaskId(), name, description);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId()))
            throw new RuntimeException("Ошибка! Задача с идентификатором " + task.getId() + " не найдена!");
        tasks.put(task.getId(), task);
    }

    @Override
    public void deleteAllTasks() {
        // Запускаем в цикле удаление задач по одной, так как нужно удалять из истории просмотра
        for (Integer taskId : tasks.keySet()) {
            deleteTask(taskId);
        }
    }

    @Override
    public void deleteTask(int taskId) {
        if (!tasks.containsKey(taskId))
            throw new RuntimeException("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public Task getTask(int taskId) {
        if (!tasks.containsKey(taskId))
            throw new RuntimeException("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
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
        if (name.isEmpty() || description.isEmpty())
            throw new RuntimeException("Ошибка! Название или описание пустое");
        Epic epic = new Epic(getNewTaskId(), name, description);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId()))
            throw new RuntimeException("Ошибка! Эпик с идентификатором " + epic.getId() + " не найден!");
        if (epics.get(epic.getId()).getStatus() != epic.getStatus())
            throw new RuntimeException("Ошибка! Самостоятельная смена статуса у Эпика запрещена!");
        epics.put(epic.getId(), epic);
    }

    @Override
    public void deleteAllEpics() {
        // Запускаем в цикле удаление Эпиков по одному, так как нужно удалять из истории просмотра
        for (Integer epicId : epics.keySet()) {
            deleteEpic(epicId);
        }
    }

    @Override
    public void deleteEpic(int epicId) {
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        if (!epics.get(epicId).getSubTasksList().isEmpty()) {
            List<Integer> subTasksOfEpic = new ArrayList<>(epics.get(epicId).getSubTasksList());
            for (Integer subTaskId : subTasksOfEpic){
                deleteSubTask(subTaskId);
            }
        }
        epics.remove(epicId);
        historyManager.remove(epicId);
    }

    @Override
    public Epic getEpic(int epicId) throws RuntimeException {
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;

    }

    @Override
    public List<Epic> getAllEpics() {
        for (int epicId : epics.keySet()) {
            historyManager.add(epics.get(epicId));
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
        if (name.isEmpty() || description.isEmpty())
            throw new RuntimeException("Ошибка! Название или описание пустое");
        if (!epics.containsKey(epicId))
            throw new RuntimeException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        SubTask subTask = new SubTask(getNewTaskId(), name, description, epicId);
        subTasks.put(subTask.getId(), subTask); //Добавляем Подзадачу
        epics.get(epicId).addSubTask(subTask.getId()); //Добавляем идентификатор подзадачи в Эпик, к которому привязываем подзадачу
        if (epics.get(epicId).getStatus() == TaskStatus.DONE) {
            calculateEpicStatus(epicId); //Если статус Эпика DONE, то нужно запустить перерасчет статуса Эпика
        }
        return subTask.getId();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId()))
            throw new RuntimeException("Ошибка! Подзадача с идентификатором " + subTask.getId() + "не найдена!");
        TaskStatus oldSubTaskStatus = subTasks.get(subTask.getId()).getStatus();
        subTasks.put(subTask.getId(), subTask);
        if (oldSubTaskStatus != subTask.getStatus()) {
            calculateEpicStatus(subTask.getEpicId()); //Если статус Подзадачи изменился, то нужно запустить перерасчет статуса Эпика
        }
    }

    @Override
    public void deleteAllSubTask() {
        // Запускаем в цикле удаление Подзадач по одной, так как нужно пересчитывать статус Эпика и удалять из истории просмотра
        for (Integer subTaskId : subTasks.keySet()) {
            deleteSubTask(subTaskId);
        }
    }

    @Override
    public void deleteSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId))
            throw new RuntimeException("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        int tempEpicId = subTasks.get(subTaskId).getEpicId();
        epics.get(tempEpicId).deleteSubTask(subTaskId); //Удаляем идентификатор удаляемой Подзадачи из Эпика
        calculateEpicStatus(tempEpicId); //Запускаем перерасчет статуса Эпика у Эпика удаляемой Подзадачи
        subTasks.remove(subTaskId); //Удаляем саму Подзадачу
        historyManager.remove(subTaskId);
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId))
            throw new RuntimeException("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
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
        HashMap<TaskStatus, Integer> subTasksStatus = new HashMap<>();
        subTasksStatus.put(TaskStatus.NEW, 0);
        subTasksStatus.put(TaskStatus.IN_PROGRESS, 0);
        subTasksStatus.put(TaskStatus.DONE, 0);
        int subTasksCountInEpic = epics.get(epicId).getSubTasksList().size();
        for (Integer subTaskId : epics.get(epicId).getSubTasksList()) {
            SubTask subTask = subTasks.get(subTaskId);
            int tempCount = 0;
            if (subTasksStatus.containsKey(subTask.getStatus())) {
                tempCount = subTasksStatus.get(subTask.getStatus());
            }
            subTasksStatus.put(subTask.getStatus(), ++tempCount);
        }
        if (subTasksCountInEpic == 0 || subTasksCountInEpic == subTasksStatus.get(TaskStatus.NEW)) {
            epics.get(epicId).setStatus(TaskStatus.NEW);
            return;
        }
        if (subTasksCountInEpic == subTasksStatus.get(TaskStatus.DONE)) {
            epics.get(epicId).setStatus(TaskStatus.DONE);
            return;
        }
        if (epics.get(epicId).getStatus() != TaskStatus.IN_PROGRESS) {
            epics.get(epicId).setStatus(TaskStatus.IN_PROGRESS);
        }
    }
}
