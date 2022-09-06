package ru.yandex.kanban;
import ru.yandex.kanban.model.enums.TaskStatus;
import ru.yandex.kanban.service.HistoryManager;
import ru.yandex.kanban.service.Managers;
import ru.yandex.kanban.service.TaskManager;
import ru.yandex.kanban.tasks.Epic;
import ru.yandex.kanban.tasks.SubTask;
import ru.yandex.kanban.tasks.Task;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefaultTaskManager();
        HistoryManager historyManager = Managers.getDefaultHistoryManager();

        int taskId1 = taskManager.addNewTask("Задача 1", "Описание задачи 1");
        int taskId2 = taskManager.addNewTask("Задача 2", "Описание задачи 2");

        int epicId1 = taskManager.addNewEpic("Эпик 1", "Описание эпика 1");
        int epicId2 = taskManager.addNewEpic("Эпик 2", "Описание эпика 2");

        int subTask1 = taskManager.addNewSubTask("Подзадача 1", "Описание подзадачи 1", epicId1);
        int subTask2 = taskManager.addNewSubTask("Подзадача 2", "Описание подзадачи 2", epicId1);
        int subTask3 = taskManager.addNewSubTask("Подзадача 3", "Описание подзадачи 3", epicId2);

        taskManager.getTask(taskId1);
        taskManager.getEpic(epicId1);
        taskManager.getSubTask(subTask1);
        taskManager.getEpic(epicId2);
        System.out.println("Список просмотров задач:");
        System.out.println(taskManager.getHistory());
        System.out.println();

        System.out.println("Список всех TaskОВ:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        System.out.println("Список всех EpicОВ:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Список всех SubTaskОВ:");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();

        Task tempTaskForDebug;
        Epic tempEpicForDebug;
        SubTask tempSubTaskForDebug;

        tempTaskForDebug = taskManager.getTask(taskId1);
        tempTaskForDebug = new Task(tempTaskForDebug.getId(), tempTaskForDebug.getName() + " (обновлено1)",
                tempTaskForDebug.getDescription() + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS);
        taskManager.updateTask(tempTaskForDebug);

        tempTaskForDebug = taskManager.getTask(taskId2);
        tempTaskForDebug = new Task(tempTaskForDebug.getId(), tempTaskForDebug.getName() + " (обновлено1)",
                tempTaskForDebug.getDescription() + " (обновлено1)", TaskStatus.taskStatus.DONE);
        taskManager.updateTask(tempTaskForDebug);

        tempEpicForDebug = taskManager.getEpic(epicId1);
        tempEpicForDebug = new Epic(tempEpicForDebug.getId(), tempEpicForDebug.getName() + " (обновлено1)",
                tempEpicForDebug.getDescription() + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS, tempEpicForDebug.getSubTasksList()
        );
        taskManager.updateEpic(tempEpicForDebug);

        tempEpicForDebug = taskManager.getEpic(epicId2);
        tempEpicForDebug = new Epic(tempEpicForDebug.getId(), tempEpicForDebug.getName() + " (обновлено1)",
                tempEpicForDebug.getDescription() + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS, tempEpicForDebug.getSubTasksList());
        taskManager.updateEpic(tempEpicForDebug);

        tempSubTaskForDebug = taskManager.getSubTask(subTask1);
        tempSubTaskForDebug = new SubTask(tempSubTaskForDebug.getId(), tempSubTaskForDebug.getName() + " (обновлено1)",
                tempSubTaskForDebug.getDescription() + " (обновлено1)", tempSubTaskForDebug.getEpicId(), TaskStatus.taskStatus.IN_PROGRESS);
        taskManager.updateSubTask(tempSubTaskForDebug);

        tempSubTaskForDebug = taskManager.getSubTask(subTask3);
        tempSubTaskForDebug = new SubTask(tempSubTaskForDebug.getId(), tempSubTaskForDebug.getName() + " (обновлено1)",
                tempSubTaskForDebug.getDescription() + " (обновлено1)", tempSubTaskForDebug.getEpicId(), TaskStatus.taskStatus.DONE);
        taskManager.updateSubTask(tempSubTaskForDebug);

        System.out.println();
        System.out.println("==========Обновили некоторые сущности==========");
        System.out.println("Список всех TaskОВ:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        System.out.println("Список всех EpicОВ:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Список всех SubTaskОВ:");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();

        taskManager.deleteTask(taskId1);
        taskManager.deleteEpic(epicId2);
        taskManager.deleteSubTask(subTask1);

        System.out.println();
        System.out.println("==========Удалили некоторые сущности==========");
        System.out.println("Список всех TaskОВ:");
        System.out.println(taskManager.getAllTasks());
        System.out.println();

        System.out.println("Список всех EpicОВ:");
        System.out.println(taskManager.getAllEpics());
        System.out.println();

        System.out.println("Список всех SubTaskОВ:");
        System.out.println(taskManager.getAllSubTasks());
        System.out.println();
    }
}
