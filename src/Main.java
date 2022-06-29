public class Main {

    public static void main(String[] args) {
        Manager.addNewTask("Задача 1", "Описание задачи 1");
        Manager.addNewTask("Задача 2", "Описание задачи 2");
        Manager.addNewEpic("Эпик 1", "Описание эпика 1");
        Manager.addNewSubTask("Подзадача 1", "Описание подзадачи 1", 3);
        Manager.addNewSubTask("Подзадача 2", "Описание подзадачи 2", 3);
        Manager.addNewEpic("Эпик 2", "Описание эпика 2");
        Manager.addNewSubTask("Подзадача 3", "Описание подзадачи 3", 6);
        System.out.println(Manager.getAllTasks());
        System.out.println(Manager.getAllEpics());
        System.out.println(Manager.getAllSubTasks());

        Task tempTaskForDebug;
        Epic tempEpicForDebug;
        SubTask tempSubTaskForDebug;

        tempTaskForDebug = Manager.getTask(1);
        tempTaskForDebug = new Task(tempTaskForDebug.id, tempTaskForDebug.name + " (обновлено1)", tempTaskForDebug.description + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS);
        Manager.updateTask(tempTaskForDebug);

        tempTaskForDebug = Manager.getTask(2);
        tempTaskForDebug = new Task(tempTaskForDebug.id, tempTaskForDebug.name + " (обновлено1)", tempTaskForDebug.description + " (обновлено1)", TaskStatus.taskStatus.DONE);
        Manager.updateTask(tempTaskForDebug);

        tempEpicForDebug = Manager.getEpic(3);
        tempEpicForDebug = new Epic(tempEpicForDebug.id, tempEpicForDebug.name + " (обновлено1)", tempEpicForDebug.description + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS, tempEpicForDebug.getSubTasksList()
        );
        Manager.updateEpic(tempEpicForDebug);

        tempEpicForDebug = Manager.getEpic(6);
        tempEpicForDebug = new Epic(tempEpicForDebug.id, tempEpicForDebug.name + " (обновлено1)", tempEpicForDebug.description + " (обновлено1)", TaskStatus.taskStatus.IN_PROGRESS, tempEpicForDebug.getSubTasksList());
        Manager.updateEpic(tempEpicForDebug);

        tempSubTaskForDebug = Manager.getSubTask(4);
        tempSubTaskForDebug = new SubTask(tempSubTaskForDebug.id, tempSubTaskForDebug.name + " (обновлено1)", tempSubTaskForDebug.description + " (обновлено1)", tempSubTaskForDebug.epicId, TaskStatus.taskStatus.IN_PROGRESS);
        Manager.updateSubTask(tempSubTaskForDebug);

        tempSubTaskForDebug = Manager.getSubTask(7);
        tempSubTaskForDebug = new SubTask(tempSubTaskForDebug.id, tempSubTaskForDebug.name + " (обновлено1)", tempSubTaskForDebug.description + " (обновлено1)", tempSubTaskForDebug.epicId, TaskStatus.taskStatus.DONE);
        Manager.updateSubTask(tempSubTaskForDebug);
        System.out.println(Manager.getAllTasks());
        System.out.println(Manager.getAllEpics());
        System.out.println(Manager.getAllSubTasks());

        Manager.deleteTask(1);
        Manager.deleteEpic(6);
        Manager.deleteSubTask(4);
        System.out.println(Manager.getAllTasks());
        System.out.println(Manager.getAllEpics());
        System.out.println(Manager.getAllSubTasks());
    }
}
