import java.util.ArrayList;
import java.util.HashMap;

public class Manager {
    protected static int lastTaskId = 0;
    protected static HashMap<Integer, Task> tasks = new HashMap<>();
    protected static HashMap<Integer, Epic> epics = new HashMap<>();
    protected static HashMap<Integer, SubTask> subTasks = new HashMap<>();

    public static int getNewTaskId() {
        return ++lastTaskId;
    }

    // Методы для TASK
    static void addNewTask(String name, String description) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return;
        }
        Task task = new Task(name, description);
        tasks.put(task.id, task);
    }

    static void updateTask(Task task) {
        if (!tasks.containsKey(task.getId())) {
            System.out.println("Ошибка! Задача с идентификатором " + task.getId() + " не найдена!");
            return;
        }
        tasks.put(task.getId(), task);
    }

    static void deleteAllTasks() {
        tasks.clear();
    }

    static void deleteTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
        }
        tasks.remove(taskId);
    }

    static Task getTask(int taskId) {
        if (!tasks.containsKey(taskId)) {
            System.out.println("Ошибка! Задача с идентификатором " + taskId + " не найдена!");
            return null;
        }
        return tasks.get(taskId);
    }

    static ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // Методы EPIC
    static void addNewEpic(String name, String description) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return;
        }
        Epic epic = new Epic(name, description);
        epics.put(epic.id, epic);
    }

    static void updateEpic(Epic epic) {
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

    static void deleteAllEpics() {
        for (Epic epic : epics.values()) {
            if (!epic.subTasksList.isEmpty()) {
                System.out.println("Ошибка! Невозможно удалить Эпик c id:" + epic.getId() + ", так как у него присутствуют подзадачи");
            } else {
                epics.remove(epic.getId());
            }
        }
    }

    static void deleteEpic(int epicId) {
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

    static Epic getEpic(int epicId) {
        if (!epics.containsKey(epicId)) System.out.println("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
        return epics.get(epicId);
    }

    static ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    static ArrayList<SubTask> getAllSubTasksByEpic(int epicId) {
        ArrayList<SubTask> subTaskList = new ArrayList<>();
        for (int subTaskId : epics.get(epicId).getSubTasksList()) {
            subTaskList.add(subTasks.get(subTaskId));
        }
        return subTaskList;
    }

    // Методы SUBTASK
    static void addNewSubTask(String name, String description, int epicId) {
        if (name.isEmpty() || description.isEmpty()) {
            System.out.println("Ошибка! Название или описание пустое");
            return;
        }
        if (!epics.containsKey(epicId)) {
            System.out.println("Ошибка! Эпик с идентификатором " + epicId + " не найден!");
            return;
        }
        SubTask subTask = new SubTask(name, description, epicId);
        subTasks.put(subTask.id, subTask); //Добавляем Подзадачу
        epics.get(epicId).addSubTask(subTask.getId()); //Добавляем идентификатор подзадачи в Эпик, к которому привязываем подзадачу
        if (epics.get(epicId).getStatus() == TaskStatus.taskStatus.DONE) {
            calculateEpicStatus(epicId); //Если статус Эпика DONE, то нужно запустить перерасчет статуса Эпика
        }
    }

    static void updateSubTask(SubTask subTask) {
        if (!subTasks.containsKey(subTask.getId())) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTask.id + "не найдена!");
            return;
        }
        TaskStatus.taskStatus oldSubTaskStatus = subTasks.get(subTask.getId()).status;
        subTasks.put(subTask.getId(), subTask);
        if (oldSubTaskStatus != subTask.status) {
            calculateEpicStatus(subTask.getEpicId()); //Если статус Подзадачи изменился, то нужно запустить перерасчет статуса Эпика
        }
    }

    static void deleteAllSubTask() {
        //Запускаем в цикле удаление Подзадач по одной, так как нужно пересчитывать статус Эпика
        for (SubTask subTask : subTasks.values()) {
            deleteSubTask(subTask.getId());
        }
    }

    static void deleteSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        }
        int tempEpicId = subTasks.get(subTaskId).getEpicId();
        epics.get(tempEpicId).deleteSubTask(subTaskId); //Удаляем идентификатор удаляемой Подзадачи из Эпика
        calculateEpicStatus(tempEpicId); //Запускаем перерасчет статуса Эпика у Эпика удаляемой Подзадачи
        subTasks.remove(subTaskId); //Удаляем саму Подзадачу
    }

    static SubTask getSubTask(int subTaskId) {
        if (!subTasks.containsKey(subTaskId)) {
            System.out.println("Ошибка! Подзадача с идентификатором " + subTaskId + " не найдена!");
        }
        return subTasks.get(subTaskId);
    }

    static ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    private static void calculateEpicStatus(int epicId) {
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
