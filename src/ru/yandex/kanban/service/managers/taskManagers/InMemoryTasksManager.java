package ru.yandex.kanban.service.managers.taskManagers;

import ru.yandex.kanban.exception.TaskManagerException;
import ru.yandex.kanban.service.managers.Managers;
import ru.yandex.kanban.service.managers.historyManagers.HistoryManager;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.time.Instant;
import java.util.*;

public class InMemoryTasksManager implements TaskManager {
    protected int lastTaskId = 0;
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistoryManager();
    protected Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
    public int addNewTask(String name, String description, long duration, Instant startTime) {
        if (name.isEmpty() || description.isEmpty())
            throw new TaskManagerException("Ошибка! Название или описание пустое");
        Task task = new Task(getNewTaskId(), name, description, duration, startTime);
        addPrioritizedTask(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public int addNewTask(Task task) {
        addPrioritizedTask(task);
        tasks.put(task.getId(), task);
        return task.getId();
    }

    @Override
    public void updateTask(Task task) {
        if (!tasks.containsKey(task.getId()))
            throw new TaskManagerException("Ошибка! Задача с идентификатором " + task.getId() + " не найдена!");
        addPrioritizedTask(task);
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
            throw new TaskManagerException("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    @Override
    public Task getTask(int taskId) {
        if (!tasks.containsKey(taskId))
            throw new TaskManagerException("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
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
            throw new TaskManagerException("Ошибка! Название или описание пустое");
        Epic epic = new Epic(getNewTaskId(), name, description);
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public int addNewEpic(Epic epic) {
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (!epics.containsKey(epic.getId()))
            throw new TaskManagerException("Ошибка! Эпик с идентификатором " + epic.getId() + " не найден!");
        if (epics.get(epic.getId()).getStatus() != epic.getStatus())
            throw new TaskManagerException("Ошибка! Самостоятельная смена статуса у Эпика запрещена!");
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
            throw new TaskManagerException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
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
    public Epic getEpic(int epicId) throws TaskManagerException {
        if (!epics.containsKey(epicId))
            throw new TaskManagerException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
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
    public int addNewSubTask(String name, String description, int epicId, long duration, Instant startTime) {
        if (name.isEmpty() || description.isEmpty())
            throw new TaskManagerException("Ошибка! Название или описание пустое");
        if (!epics.containsKey(epicId))
            throw new TaskManagerException("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        SubTask subTask = new SubTask(getNewTaskId(), name, description, epicId, duration, startTime);
        addPrioritizedTask(subTask);
        subTasks.put(subTask.getId(), subTask); //Добавляем Подзадачу
        epics.get(epicId).addSubTask(subTask.getId()); //Добавляем идентификатор подзадачи в Эпик, к которому привязываем подзадачу
        epics.get(epicId).calculateEpicStatusAndDuration(subTasks);
        return subTask.getId();
    }

    @Override
    public int addNewSubTask(SubTask subTask) {
        addPrioritizedTask(subTask);
        subTasks.put(subTask.getId(), subTask); //Добавляем Подзадачу
        epics.get(subTask.getEpicId()).addSubTask(subTask.getId()); //Добавляем идентификатор подзадачи в Эпик, к которому привязываем подзадачу
        epics.get(subTask.getEpicId()).calculateEpicStatusAndDuration(subTasks);
        return subTask.getId();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId()))
            throw new TaskManagerException("Ошибка! Подзадача с идентификатором " + subTask.getId() + "не найдена!");
        addPrioritizedTask(subTask);
        subTasks.put(subTask.getId(), subTask);
        epics.get(subTask.getEpicId()).calculateEpicStatusAndDuration(subTasks);
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
            throw new TaskManagerException("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        int tempEpicId = subTasks.get(subTaskId).getEpicId();
        epics.get(tempEpicId).deleteSubTask(subTaskId); //Удаляем идентификатор удаляемой Подзадачи из Эпика
        epics.get(tempEpicId).calculateEpicStatusAndDuration(subTasks); //Запускаем перерасчет статуса Эпика у Эпика удаляемой Подзадачи
        prioritizedTasks.remove(subTasks.get(subTaskId));
        subTasks.remove(subTaskId); //Удаляем саму Подзадачу
        historyManager.remove(subTaskId);
    }

    @Override
    public SubTask getSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId))
            throw new TaskManagerException("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
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

    private void addPrioritizedTask(Task task) {
        prioritizedTasks.add(task);
        validateTaskPriority();
    }

    private void validateTaskPriority() {
        List<Task> prioritizedTasksList = getPrioritizedTasks();
        for (int i = 1; i < prioritizedTasksList.size(); i++) {
            Task currentTask = prioritizedTasksList.get(i);
            Task prevTask = prioritizedTasksList.get(i - 1);
            if (currentTask.getStartTime().isBefore(prevTask.getEndTime())) {
                throw new TaskManagerException("Задачи с номерами " + currentTask.getId() + " и " + prevTask.getId() + " пересекаются по времени выполнения");
            }
        }
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}