package ru.yandex.kanban.service;

import org.junit.jupiter.api.Test;
import ru.yandex.kanban.exception.TaskManagerException;
import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.service.managers.taskManagers.TaskManager;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public abstract class TaskManagerTest <T extends TaskManager>  {

    protected T taskManager;

    protected Task createTask() {
        int taskId = taskManager.addNewTask("Title", "Description", 0, Instant.now());
        return taskManager.getTask(taskId);
    }

    protected Epic createEpic() {
        int epicId = taskManager.addNewEpic("Title", "Description");
        return taskManager.getEpic(epicId);
    }

    protected SubTask createSubTask(Epic epic) {
        int subTaskId = taskManager.addNewSubTask("Title", "Description", epic.getId(), 0, Instant.now());
        return taskManager.getSubTask(subTaskId);
    }

    @Test
    public void addTask() {
        Task task = createTask();
        List<Task> tasks = taskManager.getAllTasks();

        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(List.of(task), tasks);
    }

    @Test
    public void addEpic() {
        Epic epic = createEpic();
        List<Epic> epics = taskManager.getAllEpics();

        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(Collections.EMPTY_LIST, epic.getSubTasksList());
        assertEquals(List.of(epic), epics);
    }

    @Test
    public void addSubTask() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertEquals(epic.getId(), subTask.getEpicId());
        assertEquals(TaskStatus.NEW, epic.getStatus());
        assertEquals(TaskStatus.NEW, subTask.getStatus());
        assertEquals(List.of(subTask), subTasks);
        assertEquals(List.of(subTask.getId()), epic.getSubTasksList());
    }

    @Test
    public void updateTaskStatus() {
        Task task = createTask();
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(task);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getTask(task.getId()).getStatus());
    }

    @Test
    public void updateSubTaskStatusToInProgress() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        subTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getSubTask(subTask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateSubTaskStatusToDone() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        subTask.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask);

        assertEquals(TaskStatus.DONE, taskManager.getSubTask(subTask.getId()).getStatus());
        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToInProgress() {
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask(epic);
        SubTask subTask2 = createSubTask(epic);
        subTask2.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2);

        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void updateEpicStatusToDone() {
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask(epic);
        SubTask subTask2 = createSubTask(epic);
        subTask1.setStatus(TaskStatus.DONE);
        subTask2.setStatus(TaskStatus.DONE);
        taskManager.updateSubTask(subTask1);
        taskManager.updateSubTask(subTask2);

        assertEquals(TaskStatus.DONE, taskManager.getEpic(epic.getId()).getStatus());
    }

    @Test
    public void removeAllTasks() {
        createTask();
        Epic epic = createEpic();
        createSubTask(epic);
        taskManager.deleteAllTasks();
        taskManager.deleteAllSubTask();
        taskManager.deleteAllEpics();

        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubTasks());
    }

    @Test
    public void calculateStartAndEndTime() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);

        assertEquals(subTask.getStartTime(), epic.getStartTime());
        assertEquals(subTask.getEndTime(), epic.getEndTime());
    }

    @Test
    public void removeTask() {
        Task task = createTask();
        taskManager.deleteTask(task.getId());

        assertEquals(Collections.EMPTY_LIST, taskManager.getAllTasks());
    }

    @Test
    public void removeEpic() {
        Epic epic = createEpic();
        createSubTask(epic);
        createSubTask(epic);
        taskManager.deleteEpic(epic.getId());

        assertEquals(Collections.EMPTY_LIST, taskManager.getAllEpics());
        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubTasks());
    }

    @Test
    public void removeSubTask() {
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        taskManager.deleteSubTask(subTask.getId());

        assertEquals(Collections.EMPTY_LIST, taskManager.getAllSubTasks());
        assertEquals(Collections.EMPTY_LIST, taskManager.getEpic(epic.getId()).getSubTasksList());
    }

    @Test
    public void returnSubTasksByEpicId() {
        Epic epic = createEpic();
        SubTask subTask1 = createSubTask(epic);
        SubTask subTask2 = createSubTask(epic);
        SubTask subTask3 = createSubTask(epic);

        assertEquals(List.of(subTask1, subTask2, subTask3), taskManager.getAllSubTasksByEpic(epic.getId()));
    }

    @Test
    public void returnEmptyHistory() {
        assertEquals(Collections.EMPTY_LIST, taskManager.getHistory());
    }

    @Test
    public void returnHistory() {
        Task task = createTask();
        Epic epic = createEpic();
        SubTask subTask = createSubTask(epic);
        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        taskManager.getSubTask(subTask.getId());

        assertEquals(List.of(task, epic, subTask), taskManager.getHistory());
    }

    @Test
    public void exceptionIfTasksAreCross() {
        assertThrows(TaskManagerException.class, () -> {
            taskManager.addNewTask("Title", "Description", 10, Instant.ofEpochMilli(0));
            taskManager.addNewTask("Title", "Description", 10, Instant.ofEpochMilli(5));
        });
    }
}